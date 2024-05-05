package fhict.boards.service;

import fhict.boards.domain.dto.IssueCreateRequest;
import fhict.boards.domain.dto.IssueResponse;
import fhict.boards.domain.dto.IssueUpdateRequest;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.MemberRole;
import fhict.boards.exception.IssueNotFoundException;
import fhict.boards.exception.MemberNotFoundException;
import fhict.boards.exception.ProjectNotFoundException;
import fhict.boards.exception.UnauthorizedAccessException;
import fhict.boards.repository.IssueRepository;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.entity.Issue;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceImplTest {
    @InjectMocks
    private IssueServiceImpl issueService;
    @Mock
    private ProjectService projectService;
    @Mock
    private MemberService memberService;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private DtoMapper dtoMapper;
    @Captor
    private ArgumentCaptor<Issue> issueCaptor;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void createIssue_WhenProjectExists_ShouldReturnIssueResponse() {
        // Arrange
        Long projectId = 1L;
        IssueCreateRequest createRequest = new IssueCreateRequest();

        Project project = Project.builder()
                .id(projectId)
                .key("PROJ")
                .build();

        Member creator = Member.builder()
                .id(1L)
                .build();

        Issue issue = Issue.builder()
                .project(project)
                .createdBy(creator)
                .build();
        IssueResponse issueResponse = new IssueResponse();

        when(projectService.getProjectByIdOrThrowNotFound(projectId)).thenReturn(project);
        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER))).thenReturn(creator);
        when(issueRepository.findLatestIssueNumberByProjectId(projectId)).thenReturn(Optional.of(100L));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        when(dtoMapper.mapToIssueResponse(issue)).thenReturn(issueResponse);

        // Act
        IssueResponse result = issueService.createIssue(projectId, createRequest);

        // Assert
        assertEquals(issueResponse, result);
        verify(issueRepository).save(issueCaptor.capture());
        Issue savedIssue = issueCaptor.getValue();
        assertNotNull(savedIssue);
        assertEquals("PROJ-101", savedIssue.getKey());
        assertEquals(creator, savedIssue.getCreatedBy());
    }

    @Test
    void createIssue_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        IssueCreateRequest createRequest = new IssueCreateRequest();

        when(projectService.getProjectByIdOrThrowNotFound(projectId)).thenThrow(new ProjectNotFoundException(1L));

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> issueService.createIssue(projectId, createRequest));
    }

    @Test
    void createIssue_WhenUserIsNotAuthorized_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        IssueCreateRequest createRequest = new IssueCreateRequest();

        when(projectService.getProjectByIdOrThrowNotFound(projectId)).thenReturn(new Project());
        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to create an issue"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> issueService.createIssue(projectId, createRequest));
    }

    @Test
    void getIssuesByProjectId_WhenProjectExistsAndUserIsAuthorized_ShouldReturnIssues() {
        // Arrange
        Long projectId = 1L;
        Issue issue = new Issue();
        IssueResponse issueResponse = new IssueResponse();
        List<Issue> issues = Collections.singletonList(issue);
        List<IssueResponse> issueResponses = Collections.singletonList(issueResponse);

        Project project = new Project();
        project.setId(projectId);
        project.setIssues(issues);

        when(projectService.getProjectByIdOrThrowNotFound(projectId)).thenReturn(project);
        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER, MemberRole.VIEWER))).thenReturn(new Member());
        when(dtoMapper.mapToIssueResponse(issue)).thenReturn(issueResponse);

        // Act
        List<IssueResponse> result = issueService.getIssuesByProjectId(projectId);

        // Assert
        assertEquals(issueResponses, result);
    }

    @Test
    void getIssuesByProjectId_WhenProjectDoesntExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(projectService.getProjectByIdOrThrowNotFound(projectId)).thenThrow(new ProjectNotFoundException(1L));

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> issueService.getIssuesByProjectId(projectId));
    }

    @Test
    void getIssuesByProjectId_WhenUserIsNotAuthorized_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER, MemberRole.VIEWER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to view issues"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> issueService.getIssuesByProjectId(projectId));
    }

    @Test
    void getIssueByIdOrThrowNotFound_WhenIssueExists_ShouldReturnIssue() {
        // Arrange
        Long issueId = 1L;

        Issue issue = new Issue();
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        // Act
        Issue result = issueService.getIssueByIdOrThrowNotFound(issueId);

        // Assert
        assertEquals(issue, result);
    }

    @Test
    void getIssueByIdOrThrowNotFound_WhenIssueDoesNotExist_ShouldThrowException() {
        // Arrange
        Long issueId = 1L;

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> issueService.getIssueByIdOrThrowNotFound(issueId));
    }

    @Test
    void deleteIssue_WhenMemberIsAuthorized_ShouldDeleteIssue() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        Member authorizedMember = Member.builder()
                .id(1L)
                .role(MemberRole.OWNER)
                .build();

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenReturn(authorizedMember);

        // Act
        issueService.deleteIssue(issueId, projectId);

        // Assert
        verify(notificationRepository).deleteByIssueId(issueId);
        verify(issueRepository).deleteById(issueId);
    }

    @Test
    void deleteIssue_WhenMemberIsNotAuthorized_ShouldThrowException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to delete the issue"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> issueService.deleteIssue(issueId, projectId));
        verifyNoInteractions(notificationRepository, issueRepository);
    }

    @Test
    void updateIssue_WhenSuccessful_ShouldReturnIssueResponse() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        IssueUpdateRequest updateRequest = new IssueUpdateRequest();

        Member updater = Member.builder().id(1L).build();
        Member assignee = Member.builder().id(2L).build();
        Issue issue = Issue.builder().id(issueId).assignee(assignee).build();
        IssueResponse issueResponse = new IssueResponse();

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER))).thenReturn(updater);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        when(dtoMapper.mapToIssueResponse(issue)).thenReturn(issueResponse);

        // Act
        IssueResponse result = issueService.updateIssue(issueId, projectId, updateRequest);

        // Assert
        assertEquals(issueResponse, result);
        verify(issueRepository).save(issueCaptor.capture());
        Issue savedIssue = issueCaptor.getValue();
        assertNotNull(savedIssue);
    }

    @Test
    void updateIssue_WhenIssueNotFound_ShouldThrowIssueNotFoundException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        // Act & Assert
        IssueUpdateRequest request = new IssueUpdateRequest();
        assertThrows(IssueNotFoundException.class, () -> issueService.updateIssue(issueId, projectId, request));
    }

    @Test
    void updateIssue_WhenMemberIsNotAuthorized_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to update the issue"));

        // Act & Assert
        IssueUpdateRequest request = new IssueUpdateRequest();
        assertThrows(UnauthorizedAccessException.class, () -> issueService.updateIssue(issueId, projectId, request));
    }

    @Test
    void updateIssue_WhenIssueDoesNotExist_ShouldThrowException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueUpdateRequest updateRequest = new IssueUpdateRequest();

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> issueService.updateIssue(issueId, projectId, updateRequest));
    }

    @Test
    void updateIssue_WhenUserIsNotAuthorized_ShouldThrowException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueUpdateRequest updateRequest = new IssueUpdateRequest();

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to update the issue"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> issueService.updateIssue(issueId, projectId, updateRequest));
    }

    @Test
    void getIssueById_WhenMemberAuthorizedAndIssueExists_ShouldReturnIssueResponse() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        Issue issue = Issue.builder().id(issueId).build();
        IssueResponse expectedResponse = IssueResponse.builder().id(issueId).build();

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER, MemberRole.VIEWER))).thenReturn(new Member());
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(dtoMapper.mapToIssueResponse(issue)).thenReturn(expectedResponse);

        // Act
        IssueResponse result = issueService.getIssueById(issueId, projectId);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void getIssueById_WhenIssueNotFound_ShouldThrowIssueNotFoundException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER, MemberRole.VIEWER))).thenReturn(new Member());
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> issueService.getIssueById(issueId, projectId));
    }

    @Test
    void updateStatus_WithProperAuthorization_ShouldUpdateIssueStatus() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueStatus newStatus = IssueStatus.CANCELED;
        Issue issue = new Issue();
        issue.setStatus(IssueStatus.DONE);

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER))).thenReturn(null);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        when(dtoMapper.mapToIssueResponse(any(Issue.class))).thenReturn(new IssueResponse());

        // Act
        IssueResponse response = issueService.updateStatus(issueId, projectId, newStatus);

        // Assert
        verify(issueRepository).save(issueCaptor.capture());
        Issue capturedIssue = issueCaptor.getValue();
        assertEquals(newStatus, capturedIssue.getStatus());
        assertNotNull(response);
    }

    @Test
    void updateStatus_WithoutAuthorization_ShouldThrowUnauthorizedException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueStatus newStatus = IssueStatus.IN_PROGRESS;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to update status"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> issueService.updateStatus(issueId, projectId, newStatus));
    }

    @Test
    void updateStatus_WithNonExistingIssue_ShouldThrowNotFoundException() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueStatus newStatus = IssueStatus.TO_DO;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER))).thenReturn(null);
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IssueNotFoundException.class, () -> issueService.updateStatus(issueId, projectId, newStatus));
    }

    @Test
    void updateStatus_WhenSavingIssue_ShouldCallSaveOnRepository() {
        // Arrange
        Long issueId = 1L;
        Long projectId = 1L;
        IssueStatus newStatus = IssueStatus.TO_DO;
        Issue issue = new Issue();
        issue.setStatus(IssueStatus.DONE);

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.DEVELOPER))).thenReturn(null);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        // Act
        issueService.updateStatus(issueId, projectId, newStatus);

        // Assert
        verify(issueRepository).save(issueCaptor.capture());
        Issue savedIssue = issueCaptor.getValue();
        assertEquals(newStatus, savedIssue.getStatus());
    }

    @Test
    void getMemberOrThrowNotFound_WhenMemberNotFound_ShouldThrowMemberNotFoundException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 1L;

        when(memberRepository.findByIdAndProjectId(memberId, projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> issueService.getMemberOrThrowNotFound(memberId, projectId));
    }

    @Test
    void getMemberOrThrowNotFound_WhenMemberExists_ShouldReturnMember() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 1L;
        Member member = new Member();

        when(memberRepository.findByIdAndProjectId(memberId, projectId)).thenReturn(Optional.of(member));

        // Act
        Member result = issueService.getMemberOrThrowNotFound(memberId, projectId);

        // Assert
        assertEquals(member, result);
    }
}