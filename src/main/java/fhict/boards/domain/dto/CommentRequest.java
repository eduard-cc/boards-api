package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comment request")
public class CommentRequest {
    @Size(max = 500, message = "Comment must not exceed 500 characters.")
    @Schema(description = "Body of the comment", example = "This is a comment")
    private String body;
}
