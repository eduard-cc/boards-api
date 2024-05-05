package fhict.boards.controller;

import fhict.boards.domain.dto.CommentRequest;
import fhict.boards.domain.dto.CommentResponse;
import fhict.boards.service.CommentService;
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
@RequestMapping("/projects/{projectId}/issues/{issueId}/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Create a new comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Comment request") @RequestBody @Valid CommentRequest request) {
        CommentResponse comment = commentService.createComment(projectId, issueId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(summary = "Get all comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId) {
        List<CommentResponse> comments = commentService.getComments(projectId, issueId);

        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Edit a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment edited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{commentId}")
    public ResponseEntity<CommentResponse> editComment(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            @Parameter(description = "Comment request") @RequestBody @Valid CommentRequest request) {
        CommentResponse comment = commentService.editComment(projectId, issueId, commentId, request);

        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId) {
        commentService.deleteComment(projectId, issueId, commentId);

        return ResponseEntity.noContent().build();
    }
}
