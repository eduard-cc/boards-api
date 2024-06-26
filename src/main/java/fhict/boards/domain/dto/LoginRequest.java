package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
public class LoginRequest {
    @Schema(description = "Email of the user", example = "john.doe@example.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @Schema(description = "Password of the user", example = "password123")
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;
}
