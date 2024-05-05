package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private UserResponse user;
    private MemberRole role;
    private LocalDate joinedOn;
}
