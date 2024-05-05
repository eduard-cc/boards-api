package fhict.boards.service;

import fhict.boards.domain.dto.CommentRequest;
import fhict.boards.domain.dto.CommentResponse;
import fhict.boards.exception.CommentNotFoundException;
import fhict.boards.exception.MemberNotFoundException;
import fhict.boards.exception.ProjectUnauthorizedAccessException;
import fhict.boards.repository.CommentRepository;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.entity.Comment;
import fhict.boards.repository.entity.Issue;
import fhict.boards.repository.entity.Member;
import fhict.boards.security.token.AccessToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final IssueService issueService;
    private final MemberRepository memberRepository;
    private final AccessToken requestAccessToken;
    private final DtoMapper dtoMapper;

    @Override
    public CommentResponse createComment(Long projectId, Long issueId, CommentRequest request) {
        Optional<Member> commenter = memberRepository.findByUserIdAndProjectId(requestAccessToken.getUserId(), projectId);

        if (commenter.isEmpty()) {
            throw new MemberNotFoundException("Commenter is not a member of project with ID: " + projectId);
        }
        Issue issue = issueService.getIssueByIdOrThrowNotFound(issueId);

        Comment comment = Comment.builder()
                .issue(issue)
                .createdBy(commenter.get())
                .createdOn(LocalDateTime.now())
                .body(request.getBody())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return dtoMapper.mapToCommentResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getComments(Long projectId, Long issueId) {
        Optional<Member> member = memberRepository.findByUserIdAndProjectId(requestAccessToken.getUserId(), projectId);

        if (member.isEmpty()) {
            throw new MemberNotFoundException("Authenticated user is not a member of project with ID: " + projectId);
        }

        Issue issue = issueService.getIssueByIdOrThrowNotFound(issueId);

        return issue.getComments()
                .stream()
                .map(dtoMapper::mapToCommentResponse)
                .toList();
    }

    @Override
    public CommentResponse editComment(Long projectId, Long issueId, Long commentId, CommentRequest request) {
        Comment comment = getCommentOrThrowNotFound(commentId);

        Optional<Member> editor = memberRepository.findByUserIdAndProjectId(requestAccessToken.getUserId(), projectId);

        if (editor.isEmpty()) {
            throw new MemberNotFoundException("Editor is not a member of project with ID: " + projectId);
        }

        if (!Objects.equals(editor.get(), comment.getCreatedBy())) {
            throw new ProjectUnauthorizedAccessException("Member is not authorized to edit this comment.");
        }

        comment.setBody(request.getBody());
        comment.setLastUpdatedOn(LocalDateTime.now());
        commentRepository.save(comment);

        return dtoMapper.mapToCommentResponse(comment);
    }

    @Override
    public void deleteComment(Long projectId, Long issueId, Long commentId) {
        Comment comment = getCommentOrThrowNotFound(commentId);

        Optional<Member> deleter = memberRepository.findByUserIdAndProjectId(requestAccessToken.getUserId(), projectId);

        if (deleter.isEmpty()) {
            throw new MemberNotFoundException("Deleter is not a member of project with ID: " + projectId);
        }

        if (!Objects.equals(deleter.get(), comment.getCreatedBy())) {
            throw new ProjectUnauthorizedAccessException("Member is not authorized to delete this comment.");
        }

        commentRepository.deleteById(commentId);
    }

    private Comment getCommentOrThrowNotFound(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            throw new CommentNotFoundException(id);
        }
        return comment.get();
    }
}
