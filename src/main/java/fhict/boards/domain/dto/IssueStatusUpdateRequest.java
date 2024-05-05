package fhict.boards.domain.dto;

import fhict.boards.domain.enums.IssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueStatusUpdateRequest {
    @Schema(description = "New status of the issue")
    @NotNull
    private IssueStatus status;
}
