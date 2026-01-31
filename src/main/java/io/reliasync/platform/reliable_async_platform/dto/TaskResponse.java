package io.reliasync.platform.reliable_async_platform.dto;

import io.reliasync.platform.reliable_async_platform.domain.TaskState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO returned to clients after task submission.
 */
@Data
@Getter
@Setter
public class TaskResponse {

    private String taskId;
    private TaskState state;
    private int retryCount;

    public TaskResponse(String taskId, TaskState state, int retryCount) {
        this.taskId = taskId;
        this.state = state;
        this.retryCount = retryCount;
    }

    // Getter
}
