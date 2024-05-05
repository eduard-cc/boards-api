package fhict.boards.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteUsersRequest {
    @Schema(description = "List of members to invite")
    @NotEmpty(message = "List of members must not be empty.")
    private List<MemberRequest> members;
}
