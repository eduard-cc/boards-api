package fhict.boards.domain.dto;

import fhict.boards.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private MemberResponse sender;
    private MemberResponse receiver;
    private IssueResponse issue;
    private ProjectResponse project;
    private LocalDateTime timestamp;
    private boolean read;
}
