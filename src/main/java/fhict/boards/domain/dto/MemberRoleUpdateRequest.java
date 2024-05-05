package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleUpdateRequest {
    @Schema(description = "New role of the member")
    @NotNull
    private MemberRole role;
}
