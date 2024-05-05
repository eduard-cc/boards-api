package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @Schema(description = "Email of the member", example = "john.doe@example.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @Schema(description = "Role of the member")
    @NotNull
    private MemberRole role;
}
