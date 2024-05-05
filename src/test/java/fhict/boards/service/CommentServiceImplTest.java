package fhict.boards.service;

import fhict.boards.domain.dto.CommentRequest;
import fhict.boards.domain.dto.CommentResponse;
import fhict.boards.exception.CommentNotFoundException;
import fhict.boards.exception.IssueNotFoundException;
import fhict.boards.exception.MemberNotFoundException;
import fhict.boards.exception.ProjectUnauthorizedAccessException;
import fhict.boards.repository.CommentRepository;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.entity.Comment;
import fhict.boards.repository.entity.Issue;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private IssueService issueService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AccessToken requestAccessToken;
    @Mock
    private DtoMapper dtoMapper;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void createComment_WhenValidInput_ShouldCreateAndReturnCommentResponse() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Test Comment Body");

        Member commenter = Member.builder().id(1L).user(new User()).build();
        Issue issue = Issue.builder().id(issueId).build();
        Comment savedComment = Comment.builder().id(1L).createdBy(commenter).issue(issue).build();
        CommentResponse expectedResponse = CommentResponse.builder().id(1L).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(commenter));
        when(issueService.getIssueByIdOrThrowNotFound(issueId)).thenReturn(issue);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(dtoMapper.mapToCommentResponse(savedComment)).thenReturn(expectedResponse);
        when(requestAccessToken.getUserId()).thenReturn(123L);

        // Act
        CommentResponse result = commentService.createComment(projectId, issueId, commentRequest);

        // Assert
        assertEquals(expectedResponse, result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_WhenCommenterNotFound_ShouldThrowMemberNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Test Comment Body");

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> commentService.createComment(projectId, issueId, commentRequest));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_WhenIssueNotFound_ShouldThrowIssueNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Test Comment Body");

        Member commenter = Member.builder().id(1L).user(new User()).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(commenter));
        when(issueService.getIssueByIdOrThrowNotFound(issueId)).thenThrow(new IssueNotFoundException(issueId));

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> commentService.createComment(projectId, issueId, commentRequest));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getComments_WhenMemberNotFound_ShouldThrowMemberNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> commentService.getComments(projectId, issueId));
        verify(commentRepository, never()).findAll();
    }

    @Test
    void getComments_WhenIssueNotFound_ShouldThrowIssueNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Member member = Member.builder().id(1L).user(new User()).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(member));
        when(issueService.getIssueByIdOrThrowNotFound(issueId)).thenThrow(new IssueNotFoundException(issueId));

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> commentService.getComments(projectId, issueId));
        verify(commentRepository, never()).findAll();
    }

    @Test
    void getComments_WhenMemberAndIssueFound_ShouldReturnCommentResponses() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Member member = Member.builder().id(1L).user(new User()).build();
        Issue issue = Issue.builder().id(issueId).comments(List.of(Comment.builder().id(1L).body("Test Comment").build())).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(member));
        when(issueService.getIssueByIdOrThrowNotFound(issueId)).thenReturn(issue);
        when(dtoMapper.mapToCommentResponse(any(Comment.class))).thenReturn(CommentResponse.builder().id(1L).body("Test Comment").build());

        // Act
        List<CommentResponse> result = commentService.getComments(projectId, issueId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(dtoMapper, times(1)).mapToCommentResponse(any(Comment.class));
    }

    @Test
    void editComment_WhenValidInput_ShouldReturnEditedCommentResponse() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        CommentRequest request = new CommentRequest();
        request.setBody("Edited Comment Body");

        Member editor = Member.builder().id(1L).user(new User()).build();
        Comment comment = Comment.builder().id(commentId).createdBy(editor).issue(new Issue()).build();
        Comment editedComment = Comment.builder().id(commentId).createdBy(editor).issue(new Issue()).body(request.getBody()).build();
        CommentResponse expectedResponse = CommentResponse.builder().id(commentId).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(editor));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(editedComment);
        when(dtoMapper.mapToCommentResponse(any(Comment.class))).thenReturn(expectedResponse);

        // Act
        CommentResponse result = commentService.editComment(projectId, issueId, commentId, request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(dtoMapper).mapToCommentResponse(any(Comment.class));
    }

    @Test
    void editComment_WhenEditorIsNotAuthorized_ShouldThrowProjectUnauthorizedAccessException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        CommentRequest request = new CommentRequest();
        request.setBody("Edited Comment Body");

        Member editor = Member.builder().id(1L).user(new User()).build();
        Comment comment = Comment.builder().id(commentId).createdBy(Member.builder().id(2L).user(new User()).build()).issue(new Issue()).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(editor));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> commentService.editComment(projectId, issueId, commentId, request));
    }

    @Test
    void editComment_WhenCommentNotFound_ShouldThrowCommentNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        CommentRequest request = new CommentRequest();
        request.setBody("Edited Comment Body");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CommentNotFoundException.class, () -> commentService.editComment(projectId, issueId, commentId, request));
    }

    @Test
    void deleteComment_WhenDeleterIsNotMember_ShouldThrowMemberNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        Comment comment = Comment.builder().id(commentId).createdBy(Member.builder().id(2L).user(new User()).build()).issue(new Issue()).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.empty());
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> commentService.deleteComment(projectId, issueId, commentId));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void deleteComment_WhenSuccessful_ShouldDeleteComment() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        Member deleter = Member.builder().id(1L).user(new User()).build();
        Comment comment = Comment.builder().id(commentId).createdBy(deleter).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(deleter));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(projectId, issueId, commentId);

        // Assert
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteComment_WhenMemberIsNotAuthor_ShouldThrowProjectUnauthorizedAccessException() {
        // Arrange
        Long projectId = 1L;
        Long issueId = 1L;
        Long commentId = 1L;

        Member deleter = Member.builder().id(1L).user(new User()).build();
        Member commentCreator = Member.builder().id(2L).user(new User()).build();
        Comment comment = Comment.builder().id(commentId).createdBy(commentCreator).build();

        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(deleter));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> commentService.deleteComment(projectId, issueId, commentId));
    }
}