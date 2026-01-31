package io.reliasync.platform.reliable_async_platform.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AsyncTask represents a single unit of asynchronous work
 * managed by the Reliable Async Platform.
 *
 * Why this entity exists:
 * - To track execution state
 * - To retry safely without duplication
 * - To provide auditability and observability
 *
 * Every async operation MUST be persisted before execution.
 */
@Entity
@Data
@Table(name = "async_tasks")
public class AsyncTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business-level unique identifier for the task.
     * Useful for tracing across systems.
     */
    @Column(nullable = false, unique = true)
    private String taskId;

    /**
     * Idempotency key ensures the same operation
     * is not executed multiple times.
     */
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    /**
     * Source service that submitted the task.
     */
    @Column(nullable = false)
    private String sourceService;

    /**
     * Target service where the task will be executed.
     */
    @Column(nullable = false)
    private String targetService;

    /**
     * Payload required to execute the task.
     * Stored as JSON string for flexibility.
     */
    @Lob
    @Column(nullable = false)
    private String payload;

    /**
     * Current execution state of the task.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState state;

    /**
     * Number of attempts made so far.
     */
    @Column(nullable = false)
    private int retryCount;

    /**
     * Maximum number of retries allowed.
     */
    @Column(nullable = false)
    private int maxRetries;

    /**
     * Reason for last failure (if any).
     * Extremely useful for debugging.
     */
    private String lastFailureReason;

    /**
     * Timestamp when task was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when task was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically set timestamps.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Timestamp indicating when the task
     * is eligible for the next retry.
     */
    private LocalDateTime nextRetryAt;
}
