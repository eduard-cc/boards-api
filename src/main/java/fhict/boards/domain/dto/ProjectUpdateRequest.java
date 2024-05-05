package fhict.boards.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {
    @Schema(description = "Name of the project", example = "Project Alpha")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Schema(description = "Key of the project", example = "PA")
    @Size(min = 2, max = 5, message = "Key must be between 2 and 5 characters.")
    private String key;
}
