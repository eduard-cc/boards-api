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
public class IssueCreateRequest {
    @Schema(description = "Title of the issue", example = "Issue Title")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters.")
    private String title;

    @Schema(description = "Description of the issue", example = "Issue Description")
    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    @Schema(description = "ID of the member assigned to the issue", example = "1")
    private Long assigneeMemberId;

    @Schema(description = "Type of the issue")
    @NotNull
    private IssueType type;

    @Schema(description = "Status of the issue")
    @NotNull
    private IssueStatus status;

    @Schema(description = "Priority of the issue")
    @NotNull
    private IssuePriority priority;

    @Schema(description = "Due date for the issue", example = "2022-01-01")
    private LocalDate dueOn;

    @Schema(description = "ID of the user who created the issue", example = "1")
    @NotNull
    private Long createdByUserId;
}
