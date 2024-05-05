package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    @Schema(description = "ID of the project", example = "1")
    private Long id;

    @Schema(description = "Name of the project", example = "Project Alpha")
    private String name;

    @Schema(description = "Key of the project", example = "PA")
    private String key;

    @Schema(description = "Icon of the project", type = "string", format = "binary")
    private byte[] icon;
}
