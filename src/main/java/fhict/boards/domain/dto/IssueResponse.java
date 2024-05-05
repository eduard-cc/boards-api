package fhict.boards.domain.dto;

import fhict.boards.domain.enums.IssuePriority;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.IssueType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponse {
    @Schema(description = "ID of the issue", example = "1")
    private Long id;

    @Schema(description = "Key of the issue", example = "IK")
    private String key;

    @Schema(description = "Title of the issue", example = "Issue Title")
    private String title;

    @Schema(description = "Description of the issue", example = "Issue Description")
    private String description;

    @Schema(description = "Member assigned to the issue")
    private MemberResponse assignee;

    @Schema(description = "Type of the issue")
    private IssueType type;

    @Schema(description = "Status of the issue")
    private IssueStatus status;

    @Schema(description = "Priority of the issue")
    private IssuePriority priority;

    @Schema(description = "Date and time when the issue was created", example = "2022-01-01T00:00:00")
    private LocalDateTime createdOn;

    @Schema(description = "Due date for the issue", example = "2022-01-01")
    private LocalDate dueOn;

    @Schema(description = "Member who created the issue")
    private MemberResponse createdBy;

    @Schema(description = "Date and time when the issue was last updated", example = "2022-01-01T00:00:00")
    private LocalDateTime updatedOn;
}
