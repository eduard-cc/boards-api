package fhict.boards.domain.dto;

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
    private Long id;
    private MemberResponse createdBy;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
    private String body;
}
