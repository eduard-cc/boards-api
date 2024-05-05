package fhict.boards.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteUsersRequest {
    @NotEmpty(message = "List of members must not be empty.")
    private List<MemberRequest> members;
}
