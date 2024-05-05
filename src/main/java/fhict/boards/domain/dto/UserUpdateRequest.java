package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Schema(description = "Name of the user", example = "John Doe")
    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @Schema(description = "Job title of the user", example = "Software Engineer")
    @Size(max = 50, message = "Job title must not exceed 50 characters.")
    private String jobTitle;

    @Schema(description = "Company of the user", example = "Acme Corp")
    @Size(max = 50, message = "Company must not exceed 50 characters.")
    private String company;

    @Schema(description = "Location of the user", example = "San Francisco")
    @Size(max = 50, message = "Location must not exceed 50 characters.")
    private String location;
}
