package io.reliasync.platform.reliable_async_platform.controller;

import io.reliasync.platform.reliable_async_platform.domain.AsyncTask;
import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import io.reliasync.platform.reliable_async_platform.repository.AsyncTaskRepository;
import io.reliasync.platform.reliable_async_platform.service.TaskExecutorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin APIs for inspecting and managing async tasks.
 *
 * These endpoints are intended for operational use
 * and are not exposed to normal clients.
 */
@RestController
@RequestMapping("/admin/tasks")
public class AdminTaskController {

    private final AsyncTaskRepository repository;
    private final TaskExecutorService executorService;

    public AdminTaskController(
            AsyncTaskRepository repository,
            TaskExecutorService executorService) {
        this.repository = repository;
        this.executorService = executorService;
    }

    /**
     * Fetch all tasks in FAILED or DEAD_LETTER state.
     */
    @GetMapping("/failed")
    public List<AsyncTask> getFailedTasks() {
        return repository.findAll().stream()
                .filter(task ->
                        task.getState() == TaskState.RETRY_SCHEDULED
                                || task.getState() == TaskState.DEAD_LETTER)
                .toList();
    }

    /**
     * Fetch a specific task by ID.
     */
    @GetMapping("/{taskId}")
    public AsyncTask getTask(@PathVariable Long taskId) {
        return repository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalStateException("Task not found"));
    }

    /**
     * Manually retry a task.
     */
    @PostMapping("/{taskId}/retry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void retryTask(@PathVariable Long taskId) {
        executorService.execute(taskId);
    }

    /**
     * Replay a task from scratch.
     * Resets retry count and state.
     */
    @PostMapping("/{taskId}/replay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void replayTask(@PathVariable Long taskId) {

        AsyncTask task = repository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalStateException("Task not found"));

        task.setRetryCount(0);
        task.setLastFailureReason(null);
        task.setState(TaskState.RETRY_SCHEDULED);
        task.setNextRetryAt(null);

        repository.save(task);

        executorService.execute(taskId);
    }
}
