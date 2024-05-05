package fhict.boards.domain.dto;

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
public class CommentResponse {
    @Schema(description = "ID of the comment", example = "1")
    private Long id;

    @Schema(description = "Member who created the comment")
    private MemberResponse createdBy;

    @Schema(description = "Date and time when the comment was created", example = "2022-01-01T00:00:00")
    private LocalDateTime createdOn;

    @Schema(description = "Date and time when the comment was last updated", example = "2022-01-01T00:00:00")
    private LocalDateTime lastUpdatedOn;

    @Schema(description = "Body of the comment", example = "This is a comment.")
    private String body;
}
