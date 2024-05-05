package fhict.boards.controller;

import fhict.boards.domain.dto.MemberResponse;
import fhict.boards.domain.dto.MemberRoleUpdateRequest;
import fhict.boards.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "Get a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        MemberResponse member = memberService.getMemberById(memberId);

        return ResponseEntity.ok(member);
    }

    @Operation(summary = "Remove a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        memberService.removeMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update member role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @Parameter(description = "Member ID") @PathVariable Long memberId,
            @Parameter(description = "Member role update request") @RequestBody @Valid MemberRoleUpdateRequest request) {
        MemberResponse role = memberService.updateMemberRole(memberId, request);

        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Get members by project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getMembersByProjectId(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        List<MemberResponse> members = memberService.getMembersByProjectId(projectId);

        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get current member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<MemberResponse> getCurrentMember(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        MemberResponse member = memberService.getCurrentMember(userId, projectId);

        return ResponseEntity.ok(member);
    }
}
