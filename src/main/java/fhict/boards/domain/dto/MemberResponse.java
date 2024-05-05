package fhict.boards.domain.dto;

import fhict.boards.domain.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID of the member", example = "1")
    private Long id;

    @Schema(description = "User associated with the member")
    private UserResponse user;

    @Schema(description = "Role of the member")
    private MemberRole role;

    @Schema(description = "Date when the member joined", example = "2022-01-01")
    private LocalDate joinedOn;
}
