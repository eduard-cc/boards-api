package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleResponse {
    private MemberRole role;
}
