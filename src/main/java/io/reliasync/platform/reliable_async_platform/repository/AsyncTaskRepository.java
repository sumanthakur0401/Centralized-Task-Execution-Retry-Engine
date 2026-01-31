package io.reliasync.platform.reliable_async_platform.repository;

import io.reliasync.platform.reliable_async_platform.domain.AsyncTask;
import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing async task records.
 *
 * Centralized access ensures consistent
 * retry and recovery behavior.
 */
public interface AsyncTaskRepository extends JpaRepository<AsyncTask, Long> {

    Optional<AsyncTask> findByIdempotencyKey(String idempotencyKey);

    List<AsyncTask> findByState(TaskState state);

    List<AsyncTask> findByStateAndNextRetryAtBefore(
            TaskState state,
            LocalDateTime now
    );

}

