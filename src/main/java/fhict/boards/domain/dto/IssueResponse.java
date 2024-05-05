package fhict.boards.domain.dto;

import fhict.boards.domain.enums.IssuePriority;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.IssueType;
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
    private Long id;
    private String key;
    private String title;
    private String description;
    private MemberResponse assignee;

    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;

    private LocalDateTime createdOn;
    private LocalDate dueOn;
    private MemberResponse createdBy;
    private LocalDateTime updatedOn;
}
