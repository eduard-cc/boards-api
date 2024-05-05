package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleUpdateRequest {
    @NotNull
    private MemberRole role;
}
