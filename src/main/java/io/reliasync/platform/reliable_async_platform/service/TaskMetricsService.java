package io.reliasync.platform.reliable_async_platform.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

/**
 * Centralized metrics for async task processing.
 *
 * These metrics provide visibility into system behavior
 * and are critical for operations.
 */
@Service
public class TaskMetricsService {

    private final Counter tasksCreated;
    private final Counter tasksSucceeded;
    private final Counter tasksFailed;
    private final Counter taskRetries;

    public TaskMetricsService(MeterRegistry registry) {

        this.tasksCreated = Counter.builder("async.tasks.created")
                .description("Total number of async tasks created")
                .register(registry);

        this.tasksSucceeded = Counter.builder("async.tasks.succeeded")
                .description("Total number of async tasks completed successfully")
                .register(registry);

        this.tasksFailed = Counter.builder("async.tasks.failed")
                .description("Total number of async task failures")
                .register(registry);

        this.taskRetries = Counter.builder("async.tasks.retried")
                .description("Total number of async task retries")
                .register(registry);
    }

    public void incrementCreated() {
        tasksCreated.increment();
    }

    public void incrementSuccess() {
        tasksSucceeded.increment();
    }

    public void incrementFailure() {
        tasksFailed.increment();
    }

    public void incrementRetry() {
        taskRetries.increment();
    }
}

