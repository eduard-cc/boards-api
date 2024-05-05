package fhict.boards.controller;

import fhict.boards.domain.dto.IssueCreateRequest;
import fhict.boards.domain.dto.IssueResponse;
import fhict.boards.domain.dto.IssueStatusUpdateRequest;
import fhict.boards.domain.dto.IssueUpdateRequest;
import fhict.boards.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Issue created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue create request") @RequestBody @Valid IssueCreateRequest request) {
        IssueResponse issue = issueService.createIssue(projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @Operation(summary = "Get all issues by project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<List<IssueResponse>> getIssuesByProjectId(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        List<IssueResponse> issues = issueService.getIssuesByProjectId(projectId);

        return ResponseEntity.ok(issues);
    }

    @Operation(summary = "Get an issue by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("{issueId}")
    public ResponseEntity<IssueResponse> getIssueById(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId) {
        IssueResponse issue = issueService.getIssueById(issueId, projectId);

        return ResponseEntity.ok(issue);
    }

    @Operation(summary = "Update an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("{issueId}")
    public ResponseEntity<IssueResponse> updateIssue(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Issue update request") @RequestBody @Valid IssueUpdateRequest request) {
        IssueResponse issue = issueService.updateIssue(issueId, projectId, request);

        return ResponseEntity.ok(issue);
    }

    @Operation(summary = "Update issue status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{issueId}/status")
    public ResponseEntity<IssueResponse> updateStatus(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Issue status update request") @RequestBody @Valid IssueStatusUpdateRequest request) {
        IssueResponse issue = issueService.updateStatus(issueId, projectId, request.getStatus());

        return ResponseEntity.ok(issue);
    }

    @Operation(summary = "Delete an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Issue deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{issueId}")
    public ResponseEntity<Void> deleteIssue(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId) {
        issueService.deleteIssue(issueId, projectId);

        return ResponseEntity.noContent().build();
    }
}
