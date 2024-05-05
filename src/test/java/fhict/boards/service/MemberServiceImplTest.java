package fhict.boards.service;

import fhict.boards.domain.dto.MemberResponse;
import fhict.boards.domain.dto.MemberRoleUpdateRequest;
import fhict.boards.domain.enums.MemberRole;
import fhict.boards.exception.MemberNotFoundException;
import fhict.boards.exception.ProjectNotFoundException;
import fhict.boards.exception.ProjectUnauthorizedAccessException;
import fhict.boards.repository.*;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.Project;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private AccessToken requestAccessToken;
    @Mock
    private CommentRepository commentRepository;
    @Test
    void getMemberById_WhenMemberExists_ShouldReturnMemberResponse() {
        // Arrange
        Long memberId = 1L;
        Member member = new Member();
        MemberResponse memberResponse = new MemberResponse();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(dtoMapper.mapToMemberResponse(member)).thenReturn(memberResponse);

        // Act
        MemberResponse result = memberService.getMemberById(memberId);

        // Assert
        assertEquals(memberResponse, result);
    }

    @Test
    void getMemberById_WhenMemberDoesNotExist_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(memberId));
    }

    @Test
    void getMembersByProjectId_WhenProjectExists_ShouldReturnMembers() {
        // Arrange
        Long projectId = 1L;
        Project project = mock(Project.class);
        Member member = new Member();
        MemberResponse memberResponse = new MemberResponse();
        List<MemberResponse> expected = Collections.singletonList(memberResponse);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(project.getMembers()).thenReturn(Collections.singletonList(member));
        when(dtoMapper.mapToMemberResponse(member)).thenReturn(memberResponse);

        // Act
        List<MemberResponse> result = memberService.getMembersByProjectId(projectId);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getMembersByProjectId_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> memberService.getMembersByProjectId(projectId));
    }

    @Test
    void removeMember_WhenCalled_ShouldRemoveMember() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;

        Member authenticatedMember = Member.builder()
                .id(5L)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        Member memberToRemove = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.VIEWER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberToRemove));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        when(memberRepository.countByProjectId(projectId)).thenReturn(2L);
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);
        doNothing().when(notificationRepository).deleteByMemberId(anyLong());
        doNothing().when(issueRepository).setAssigneeAndCreatedByToNull(anyLong());

        // Act
        memberService.removeMember(memberId);

        // Assert
        verify(memberRepository, times(1)).deleteById(memberId);
        verify(notificationRepository, times(1)).deleteByMemberId(memberId);
    }

    @Test
    void removeMember_WhenAdminTriesToRemoveAdmin_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;

        Member authenticatedMember = Member.builder()
                .id(5L)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        Member adminToRemove = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(adminToRemove));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        when(memberRepository.countByProjectId(projectId)).thenReturn(2L);
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.removeMember(memberId));

        // Verify
        verify(memberRepository, never()).deleteById(anyLong());
        verify(notificationRepository, never()).deleteByMemberId(anyLong());
    }

    @Test
    void removeMember_WhenAdminTriesToRemoveOwner_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;

        Member authenticatedMember = Member.builder()
                .id(5L)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        Member ownerToRemove = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(ownerToRemove));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        when(memberRepository.countByProjectId(projectId)).thenReturn(2L);
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.removeMember(memberId));

        // Verify
        verify(memberRepository, never()).deleteById(anyLong());
        verify(notificationRepository, never()).deleteByMemberId(anyLong());
    }

    @Test
    void removeMember_WhenOwnerTriesToRemoveHimselfAndHasMultipleMembers_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;

        Member authenticatedMember = Member.builder()
                .id(authenticatedUserId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        Member ownerToRemove = Member.builder()
                .id(memberId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        lenient().when(memberRepository.findById(memberId)).thenReturn(Optional.of(ownerToRemove));
        lenient().when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        lenient().when(memberRepository.countByProjectId(projectId)).thenReturn(3L);
        lenient().when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.removeMember(memberId));

        // Verify
        verify(memberRepository, never()).deleteById(anyLong());
        verify(notificationRepository, never()).deleteByMemberId(anyLong());
    }

    @Test
    void removeMember_WhenOwnerTriesToRemoveHimselfAndIsTheOnlyMember_ShouldRemoveMemberAndProject() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;

        Member authenticatedMember = Member.builder()
                .id(authenticatedUserId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        Member ownerToRemove = Member.builder()
                .id(memberId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        lenient().when(memberRepository.findById(memberId)).thenReturn(Optional.of(ownerToRemove));
        lenient().when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        lenient().when(memberRepository.countByProjectId(projectId)).thenReturn(1L);
        lenient().when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act
        memberService.removeMember(memberId);

        // Verify
        verify(memberRepository, times(1)).deleteById(memberId);
        verify(notificationRepository, times(1)).deleteByMemberId(memberId);
        verify(notificationRepository, times(1)).deleteByProjectId(projectId);
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void updateMemberRole_WhenMemberExists_ShouldUpdateAndReturnMemberRoleResponse() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;
        MemberRoleUpdateRequest request = new MemberRoleUpdateRequest(MemberRole.DEVELOPER);
        MemberResponse memberResponse = MemberResponse.builder().role(request.getRole()).build();

        Member member = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.VIEWER)
                .build();

        Member authenticatedMember = Member.builder()
                .id(6L)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(authenticatedMember));
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);
        when(dtoMapper.mapToMemberResponse(member)).thenReturn(memberResponse);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MemberResponse result = memberService.updateMemberRole(memberId, request);

        // Assert
        assertEquals(memberResponse.getRole(), result.getRole());
    }


    @Test
    void updateMemberRole_WhenMemberDoesNotExist_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        MemberRoleUpdateRequest request = new MemberRoleUpdateRequest(MemberRole.VIEWER);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMemberRole(memberId, request));
    }

    @Test
    void updateMemberRole_WhenAdminTriesToChangeOtherAdminRole_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;
        MemberRoleUpdateRequest request = new MemberRoleUpdateRequest(MemberRole.DEVELOPER);

        Member adminMember = Member.builder()
                .id(authenticatedUserId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        Member targetAdminMember = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(targetAdminMember));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(adminMember));
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.updateMemberRole(memberId, request));
    }

    @Test
    void updateMemberRole_WhenAdminTriesToChangeOwnerRole_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;
        MemberRoleUpdateRequest request = new MemberRoleUpdateRequest(MemberRole.DEVELOPER);

        Member adminMember = Member.builder()
                .id(authenticatedUserId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.ADMIN)
                .build();

        Member targetOwnerMember = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(targetOwnerMember));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(adminMember));
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.updateMemberRole(memberId, request));
    }

    @Test
    void updateMemberRole_WhenOwnerTriesToChangeRoleToOwner_ShouldChangeToAdmin() {
        // Arrange
        Long memberId = 1L;
        Long projectId = 3L;
        Long authenticatedUserId = 4L;
        MemberRoleUpdateRequest request = new MemberRoleUpdateRequest(MemberRole.OWNER);

        Member ownerMember = Member.builder()
                .id(authenticatedUserId)
                .user(User.builder().id(authenticatedUserId).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.OWNER)
                .build();

        Member targetMember = Member.builder()
                .id(memberId)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.DEVELOPER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(targetMember));
        when(memberRepository.findByUserIdAndProjectId(authenticatedUserId, projectId)).thenReturn(Optional.of(ownerMember));
        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act
        memberService.updateMemberRole(memberId, request);

        // Assert
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void assertMemberIsAuthorized_WhenNotAuthorized_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        EnumSet<MemberRole> rolesAllowed = EnumSet.of(MemberRole.ADMIN, MemberRole.DEVELOPER);

        Member member = Member.builder()
                .id(1L)
                .user(User.builder().id(2L).build())
                .project(Project.builder().id(projectId).build())
                .role(MemberRole.VIEWER)
                .build();

        when(requestAccessToken.getUserId()).thenReturn(2L);
        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.of(member));

        // Act & Assert
        assertThrows(ProjectUnauthorizedAccessException.class, () -> memberService.assertMemberIsAuthorized(projectId, rolesAllowed));
    }

    @Test
    void assertMemberIsAuthorized_WhenMemberNotFound_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        EnumSet<MemberRole> rolesAllowed = EnumSet.of(MemberRole.ADMIN, MemberRole.DEVELOPER);

        when(requestAccessToken.getUserId()).thenReturn(2L);
        when(memberRepository.findByUserIdAndProjectId(any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.assertMemberIsAuthorized(projectId, rolesAllowed));
    }


    @Test
    void getCurrentMember_WhenMemberExists_ShouldReturnMemberResponse() {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;
        Member member = new Member();
        MemberResponse memberResponse = new MemberResponse();

        when(memberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(member));
        when(dtoMapper.mapToMemberResponse(member)).thenReturn(memberResponse);

        // Act
        MemberResponse result = memberService.getCurrentMember(userId, projectId);

        // Assert
        assertEquals(memberResponse, result);
    }

    @Test
    void getCurrentMember_WhenMemberDoesNotExist_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;

        when(memberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.getCurrentMember(userId, projectId));
    }

    @Test
    void getMemberByIdOrThrowNotFound_WhenMemberExists_ShouldReturnMember() {
        // Arrange
        Long memberId = 1L;
        Member member = new Member();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        Member result = memberService.getMemberByIdOrThrowNotFound(memberId);

        // Assert
        assertEquals(member, result);
    }

    @Test
    void getMemberByIdOrThrowNotFound_WhenMemberDoesNotExist_ShouldThrowException() {
        // Arrange
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberByIdOrThrowNotFound(memberId));
    }
}