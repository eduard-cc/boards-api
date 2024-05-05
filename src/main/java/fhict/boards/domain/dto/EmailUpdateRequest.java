package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailUpdateRequest {
    @Schema(description = "New email for the user", example = "john.doe@example.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String newEmail;
}
