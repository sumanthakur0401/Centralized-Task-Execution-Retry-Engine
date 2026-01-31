package io.reliasync.platform.reliable_async_platform.scheduler;

import io.reliasync.platform.reliable_async_platform.domain.AsyncTask;
import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import io.reliasync.platform.reliable_async_platform.repository.AsyncTaskRepository;
import io.reliasync.platform.reliable_async_platform.service.TaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically retries failed tasks that are eligible for retry.
 *
 * This decouples retry logic from request handling
 * and ensures controlled execution.
 */
@Component
public class RetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(RetryScheduler.class);

    private final AsyncTaskRepository repository;
    private final TaskExecutorService executorService;

    public RetryScheduler(
            AsyncTaskRepository repository,
            TaskExecutorService executorService) {
        this.repository = repository;
        this.executorService = executorService;
    }

    /**
     * Runs every 10 seconds.
     * Picks tasks in RETRY_SCHEDULED state and retries them.
     */
    @Scheduled(fixedDelay = 5_000)
    public void retryFailedTasks() {

        List<AsyncTask> tasks =
                repository.findByStateAndNextRetryAtBefore(
                        TaskState.RETRY_SCHEDULED,
                        LocalDateTime.now()
                );

        if (tasks.isEmpty()) {
            return;
        }

        log.info("RetryScheduler picked {} eligible task(s)", tasks.size());

        for (AsyncTask task : tasks) {
            executorService.execute(task.getId());
        }
    }

}
