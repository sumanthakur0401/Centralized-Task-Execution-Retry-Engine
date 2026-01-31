package io.reliasync.platform.reliable_async_platform.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used by clients to submit an async task.
 *
 * This defines the external contract of the platform.
 */
@Data
public class CreateTaskRequest {

    @NotBlank
    private String taskId;

    @NotBlank
    private String idempotencyKey;

    @NotBlank
    private String sourceService;

    @NotBlank
    private String targetService;

    @NotBlank
    private String payload;

    /**
     * Maximum number of retries allowed for this task.
     */
    @NotNull
    private Integer maxRetries;
}