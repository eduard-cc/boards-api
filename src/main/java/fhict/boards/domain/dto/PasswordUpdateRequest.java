package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    @Schema(description = "Current password of the user", example = "password123")
    @NotBlank(message = "Current password is required.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String currentPassword;

    @Schema(description = "New password for the user", example = "newpassword123")
    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String newPassword;
}
