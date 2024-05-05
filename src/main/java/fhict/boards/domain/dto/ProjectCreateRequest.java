package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {
    @Schema(description = "Name of the project", example = "Project Alpha")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Schema(description = "Key of the project", example = "PA")
    @Size(min = 2, max = 5, message = "Key must be between 2 and 5 characters.")
    private String key;

    @Schema(description = "List of members")
    @NotEmpty(message = "List of members must not be empty.")
    private List<MemberRequest> members;
}