package fhict.boards.controller;

import fhict.boards.domain.dto.CommentRequest;
import fhict.boards.domain.dto.CommentResponse;
import fhict.boards.service.CommentService;
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

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long projectId, @PathVariable Long issueId,
                                                         @RequestBody @Valid CommentRequest request) {
        CommentResponse comment = commentService.createComment(projectId, issueId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long projectId, @PathVariable Long issueId) {
        List<CommentResponse> comments = commentService.getComments(projectId, issueId);

        return ResponseEntity.ok(comments);
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<CommentResponse> editComment(@PathVariable Long projectId, @PathVariable Long issueId,
                                                       @PathVariable Long commentId, @RequestBody @Valid CommentRequest request) {
        CommentResponse comment = commentService.editComment(projectId, issueId, commentId, request);

        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long projectId, @PathVariable Long issueId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(projectId, issueId, commentId);

        return ResponseEntity.noContent().build();
    }
}
