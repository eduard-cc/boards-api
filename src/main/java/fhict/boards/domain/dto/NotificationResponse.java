package fhict.boards.domain.dto;

import fhict.boards.domain.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID of the notification", example = "1")
    private Long id;

    @Schema(description = "Type of the notification")
    private NotificationType type;

    @Schema(description = "Member who sent the notification")
    private MemberResponse sender;

    @Schema(description = "Member who received the notification")
    private MemberResponse receiver;

    @Schema(description = "Issue related to the notification")
    private IssueResponse issue;

    @Schema(description = "Project related to the notification")
    private ProjectResponse project;

    @Schema(description = "Timestamp of the notification", example = "2022-01-01T00:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Read status of the notification", example = "false")
    private boolean read;
}
