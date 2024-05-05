package fhict.boards.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailUpdateRequest {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String newEmail;
}
