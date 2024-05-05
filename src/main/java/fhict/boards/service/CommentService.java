package fhict.boards.service;

import fhict.boards.domain.dto.CommentRequest;
import fhict.boards.domain.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long projectId, Long issueId, CommentRequest request);
    List<CommentResponse> getComments(Long projectId, Long issueId);
    CommentResponse editComment(Long projectId, Long issueId, Long commentId, CommentRequest request);
    void deleteComment(Long projectId, Long issueId, Long commentId);
}
