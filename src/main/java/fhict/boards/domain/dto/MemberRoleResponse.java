package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleResponse {
    @Schema(description = "Role of the member")
    private MemberRole role;
}
