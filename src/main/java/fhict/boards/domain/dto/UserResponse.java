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
public class UserResponse {
    @Schema(description = "ID of the user", example = "1")
    private Long id;

    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Company of the user", example = "Acme Corp")
    private String company;

    @Schema(description = "Location of the user", example = "San Francisco")
    private String location;

    @Schema(description = "Picture of the user", type = "string", format = "binary")
    private byte[] picture;
}
