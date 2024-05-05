package fhict.boards.controller;

import fhict.boards.domain.dto.MemberResponse;
import fhict.boards.domain.dto.MemberRoleUpdateRequest;
import fhict.boards.service.MemberService;
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

    @GetMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberId) {
        MemberResponse member = memberService.getMemberById(memberId);

        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) {
        memberService.removeMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long memberId,
                                                           @RequestBody @Valid MemberRoleUpdateRequest request) {
        MemberResponse role = memberService.updateMemberRole(memberId, request);

        return ResponseEntity.ok(role);
    }

    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getMembersByProjectId(@PathVariable Long projectId) {
        List<MemberResponse> members = memberService.getMembersByProjectId(projectId);

        return ResponseEntity.ok(members);
    }

    @GetMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<MemberResponse> getCurrentMember(@PathVariable Long userId, @PathVariable Long projectId) {
        MemberResponse member = memberService.getCurrentMember(userId, projectId);

        return ResponseEntity.ok(member);
    }
}
