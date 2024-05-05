package fhict.boards.domain.dto;

import fhict.boards.domain.enums.IssuePriority;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.IssueType;
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
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters.")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    private Long assigneeMemberId;
    @NotNull
    private IssueType type;

    @NotNull
    private IssueStatus status;

    @NotNull
    private IssuePriority priority;

    private LocalDate dueOn;

    @NotNull
    private Long createdByUserId;
}
