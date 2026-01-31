package io.reliasync.platform.reliable_async_platform.service;

import io.reliasync.platform.reliable_async_platform.domain.AsyncTask;
import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import io.reliasync.platform.reliable_async_platform.repository.AsyncTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TaskExecutorService {

    private final TaskMetricsService metricsService;

    private final IdempotencyService idempotencyService;

    private static final Logger log = LoggerFactory.getLogger(TaskExecutorService.class);

    private final AsyncTaskRepository repository;

    public TaskExecutorService(TaskMetricsService metricsService, AsyncTaskRepository repository,IdempotencyService idempotencyService) {
        this.repository = repository;
        this.idempotencyService = idempotencyService;
        this.metricsService = metricsService;
    }

    /**
     * Executes task in a NEW transaction.
     * This ensures state transitions are committed independently.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long taskId) {

        AsyncTask task = repository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));

        // Idempotency check
        if (!idempotencyService.acquire(task.getIdempotencyKey())) {
            log.info(
                    "Skipping execution, idempotency key already processed: {}",
                    task.getIdempotencyKey()
            );
            return;
        }

        try {
            task.setState(TaskState.IN_PROGRESS);
            repository.save(task);

            simulateExecution(task);

            task.setState(TaskState.SUCCESS);
            metricsService.incrementSuccess();
            idempotencyService.markCompleted(task.getIdempotencyKey());
        } catch (Exception ex) {

            int retryCount = task.getRetryCount() + 1;
            task.setRetryCount(retryCount);
            metricsService.incrementRetry();
            task.setLastFailureReason(ex.getMessage());

            if (retryCount < task.getMaxRetries()) {

                task.setState(TaskState.RETRY_SCHEDULED);

                // Exponential backoff calculation
                long baseDelaySeconds = 5;
                long delay = (long) (baseDelaySeconds * Math.pow(2, retryCount));

                task.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));

                log.warn(
                        "Task scheduled for retry taskId={} retryCount={} nextRetryAt={}",
                        task.getTaskId(),
                        retryCount,
                        task.getNextRetryAt()
                );

            } else {
                task.setState(TaskState.DEAD_LETTER);
                log.error("Task moved to DEAD_LETTER taskId={}", task.getTaskId());
            }
        }

        repository.save(task);
    }

    private void simulateExecution(AsyncTask task) {
        if (task.getPayload().contains("fail")) {
            throw new RuntimeException("Simulated downstream failure");
        }
    }
}
