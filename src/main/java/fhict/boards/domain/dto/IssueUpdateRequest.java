package fhict.boards.domain.dto;

import fhict.boards.domain.enums.IssuePriority;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.IssueType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueUpdateRequest {
    @Schema(description = "New title of the issue", example = "Updated Issue Title")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters.")
    private String title;

    @Schema(description = "New description of the issue", example = "Updated Issue Description")
    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    @Schema(description = "ID of the member assigned to the issue", example = "1")
    private Long assigneeMemberId;

    @Schema(description = "New type of the issue")
    @NotNull
    private IssueType type;

    @Schema(description = "New status of the issue")
    @NotNull
    private IssueStatus status;

    @Schema(description = "New priority of the issue")
    @NotNull
    private IssuePriority priority;

    @Schema(description = "New due date for the issue", example = "2022-01-01")
    private LocalDate dueOn;
}
