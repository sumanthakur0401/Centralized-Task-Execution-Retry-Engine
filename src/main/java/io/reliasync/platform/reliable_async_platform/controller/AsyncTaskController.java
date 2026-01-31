package io.reliasync.platform.reliable_async_platform.controller;

import io.reliasync.platform.reliable_async_platform.dto.CreateTaskRequest;
import io.reliasync.platform.reliable_async_platform.dto.TaskResponse;
import io.reliasync.platform.reliable_async_platform.service.AsyncTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing APIs for async task management.
 */
@RestController
@RequestMapping("/api/tasks")
public class AsyncTaskController {

    private final AsyncTaskService taskService;

    public AsyncTaskController(AsyncTaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Submit a new async task.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }
}

