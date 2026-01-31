package io.reliasync.platform.reliable_async_platform.service;


import io.reliasync.platform.reliable_async_platform.domain.AsyncTask;
import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import io.reliasync.platform.reliable_async_platform.dto.CreateTaskRequest;
import io.reliasync.platform.reliable_async_platform.dto.TaskResponse;
import io.reliasync.platform.reliable_async_platform.repository.AsyncTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing async tasks.
 * <p>
 * This layer owns orchestration logic and ensures
 * consistency and correctness.
 */
@Service
public class AsyncTaskService {

    private final TaskMetricsService metricsService;
    private final AsyncTaskRepository repository;
    private final TaskExecutorService executorService;

    public AsyncTaskService(
            AsyncTaskRepository repository,
            TaskExecutorService executorService,
            TaskMetricsService metricsService) {
        this.repository = repository;
        this.executorService = executorService;
        this.metricsService = metricsService;
    }

    /**
     * Creates a new async task.
     * If a task with the same idempotency key exists,
     * the existing task is returned instead.
     */

    public TaskResponse createTask(CreateTaskRequest request) {

        return repository.findByIdempotencyKey(request.getIdempotencyKey())
                .map(existing ->
                        new TaskResponse(
                                existing.getTaskId(),
                                existing.getState(),
                                existing.getRetryCount()))
                .orElseGet(() -> {

                    AsyncTask task = new AsyncTask();
                    task.setTaskId(request.getTaskId());
                    task.setIdempotencyKey(request.getIdempotencyKey());
                    task.setSourceService(request.getSourceService());
                    task.setTargetService(request.getTargetService());
                    task.setPayload(request.getPayload());
                    task.setState(TaskState.RECEIVED);
                    task.setRetryCount(0);
                    task.setMaxRetries(request.getMaxRetries());

                    AsyncTask saved = repository.saveAndFlush(task);
                    executorService.execute(saved.getId());

                    metricsService.incrementCreated();

                    return new TaskResponse(
                            saved.getTaskId(),
                            saved.getState(),
                            saved.getRetryCount());
                });
    }
}

