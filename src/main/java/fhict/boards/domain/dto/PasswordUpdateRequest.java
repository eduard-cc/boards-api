package fhict.boards.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    @NotBlank(message = "Current password is required.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String newPassword;
}
