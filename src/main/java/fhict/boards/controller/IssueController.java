package fhict.boards.controller;

import fhict.boards.domain.dto.IssueCreateRequest;
import fhict.boards.domain.dto.IssueResponse;
import fhict.boards.domain.dto.IssueStatusUpdateRequest;
import fhict.boards.domain.dto.IssueUpdateRequest;
import fhict.boards.service.IssueService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/issues")
@AllArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@PathVariable Long projectId, @RequestBody @Valid IssueCreateRequest request) {
        IssueResponse issue = issueService.createIssue(projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getIssuesByProjectId(@PathVariable Long projectId) {
        List<IssueResponse> issues = issueService.getIssuesByProjectId(projectId);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("{issueId}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long projectId, @PathVariable Long issueId) {
        IssueResponse issue = issueService.getIssueById(issueId, projectId);

        return ResponseEntity.ok(issue);
    }

    @PutMapping("{issueId}")
    public ResponseEntity<IssueResponse> updateIssue(@PathVariable Long projectId, @PathVariable Long issueId,
                                                     @RequestBody @Valid IssueUpdateRequest request) {
        IssueResponse issue = issueService.updateIssue(issueId, projectId, request);

        return ResponseEntity.ok(issue);
    }

    @PatchMapping("{issueId}/status")
    public ResponseEntity<IssueResponse> updateStatus(@PathVariable Long projectId, @PathVariable Long issueId,
                                                      @RequestBody @Valid IssueStatusUpdateRequest request) {
        IssueResponse issue = issueService.updateStatus(issueId, projectId, request.getStatus());

        return ResponseEntity.ok(issue);
    }

    @DeleteMapping("{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long projectId, @PathVariable Long issueId) {
        issueService.deleteIssue(issueId, projectId);

        return ResponseEntity.noContent().build();
    }
}
