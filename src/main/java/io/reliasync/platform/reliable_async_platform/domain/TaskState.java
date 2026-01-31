package io.reliasync.platform.reliable_async_platform.domain;

/**
 * Represents the lifecycle of an asynchronous task.
 *
 * Having explicit states allows:
 * - Safe retries
 * - Clear debugging
 * - Deterministic recovery
 *
 * This is critical in distributed systems where failures are normal.
 */
public enum TaskState {

    /**
     * Task has been accepted by the platform
     * but execution has not started yet.
     */
    RECEIVED,

    /**
     * Task is currently being executed.
     */
    IN_PROGRESS,

    /**
     * Task executed successfully.
     */
    SUCCESS,

    /**
     * Task execution failed.
     * Failure reason will be captured.
     */
    FAILED,

    /**
     * Task is scheduled for retry
     * after a backoff period.
     */
    RETRY_SCHEDULED,

    /**
     * Task has exhausted all retries
     * and requires manual intervention.
     */
    DEAD_LETTER
}

