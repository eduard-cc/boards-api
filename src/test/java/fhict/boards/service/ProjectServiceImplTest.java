package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.domain.enums.MemberRole;
import fhict.boards.exception.*;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.ProjectRepository;
import fhict.boards.repository.UserRepository;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.Notification;
import fhict.boards.repository.entity.Project;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private AccessToken requestAccessToken;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void createProject_WhenValidRequest_ShouldCreateAndReturnProjectResponse() throws IOException {
        // Arrange
        ProjectCreateRequest request = new ProjectCreateRequest("ProjectName", "ProjectKey", Collections.emptyList());
        MultipartFile icon = null;

        when(projectRepository.save(any())).thenReturn(new Project());
        when(requestAccessToken.getUserId()).thenReturn(123L);
        when(dtoMapper.mapToProjectResponse(any())).thenReturn(new ProjectResponse());

        // Act
        ProjectResponse result = projectService.createProject(request, icon);

        // Assert
        assertNotNull(result);
        assertEquals(ProjectResponse.class, result.getClass());
    }

    @Test
    void createProject_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        MemberRequest memberRequest = new MemberRequest("email@example.com", MemberRole.ADMIN);
        ProjectCreateRequest request = new ProjectCreateRequest("ProjectName", "ProjectKey", Collections.singletonList(memberRequest));
        MultipartFile icon = null;

        when(requestAccessToken.getUserId()).thenReturn(123L);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> projectService.createProject(request, icon));
    }

    @Test
    void getProjectsByUserId_WhenUserExists_ShouldReturnProjects() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        Member member = new Member();
        Project project = new Project();
        member.setProject(project);
        List<Member> members = Collections.singletonList(member);
        ProjectResponse projectResponse = new ProjectResponse();

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(memberRepository.findByUserId(userId)).thenReturn(members);
        when(dtoMapper.mapToProjectResponse(project)).thenReturn(projectResponse);

        // Act
        List<ProjectResponse> result = projectService.getProjectsByUserId(userId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(projectResponse, result.get(0));
    }

    @Test
    void getProjectsByUserId_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        Long userId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> projectService.getProjectsByUserId(userId));
    }

    @Test
    void getProjectsByUserId_WhenUserUnauthorized_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long authenticatedUserId = 1L;
        Long specifiedUserId = 2L;

        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> projectService.getProjectsByUserId(specifiedUserId));
    }

    @Test
    void getProjectById_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void getProjectById_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        // Arrange
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void updateProjectIcon_WhenProjectExists_ShouldUpdateAndReturnIcon() throws IOException {
        // Arrange
        Long projectId = 1L;
        MultipartFile file = new MockMultipartFile("icon", "iconFile".getBytes());
        Project project = new Project();

        when(memberService.assertMemberIsAuthorized(eq(projectId), any(EnumSet.class))).thenReturn(new Member());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        byte[] result = projectService.updateProjectIcon(projectId, file);

        // Assert
        assertNotNull(result);
        assertEquals("iconFile", new String(result));
    }

    @Test
    void updateProjectIcon_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        MultipartFile file = new MockMultipartFile("icon", "iconFile".getBytes());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProjectIcon(projectId, file));
    }

    @Test
    void deleteProjectIcon_WhenProjectExists_ShouldRemoveIcon() {
        // Arrange
        Long projectId = 1L;
        Project project = Project.builder().icon(new byte[0]).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        projectService.deleteProjectIcon(projectId);

        // Assert
        assertNull(project.getIcon());
        verify(projectRepository).save(project);
    }

    @Test
    void deleteProjectIcon_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProjectIcon(projectId));
    }

    @Test
    void updateProjectDetails_WhenProjectExists_ShouldUpdateAndReturnProjectResponse() {
        // Arrange
        Long projectId = 1L;
        ProjectUpdateRequest request = new ProjectUpdateRequest("NewProjectName", "NewProjectKey");
        Project project = new Project();
        ProjectResponse projectResponse = new ProjectResponse();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(dtoMapper.mapToProjectResponse(project)).thenReturn(projectResponse);

        // Act
        ProjectResponse result = projectService.updateProjectDetails(projectId, request);

        // Assert
        assertEquals(projectResponse, result);
        assertEquals(request.getName(), project.getName());
        assertEquals(request.getKey(), project.getKey());
    }

    @Test
    void updateProjectDetails_WhenProjectDoesNotExist_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        ProjectUpdateRequest request = new ProjectUpdateRequest("NewProjectName", "NewProjectKey");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProjectDetails(projectId, request));
    }

    @Test
    void createProject_WhenDuplicateName_ShouldThrowProjectNameAlreadyExistsException() {
        // Arrange
        ProjectCreateRequest request = new ProjectCreateRequest();
        MultipartFile icon = null;

        when(requestAccessToken.getUserId()).thenReturn(1L);
        when(projectRepository.findFirstByNameOrKeyAndMembers_User_Id(any(), any(), any())).thenReturn(Optional.of(new Project()));

        // Act & Assert
        assertThrows(ProjectNameAlreadyExistsException.class, () -> projectService.createProject(request, icon));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createAndSendNotification(any(Notification.class));
    }

    @Test
    void deleteProject_WhenAuthorized_ShouldDeleteProject() {
        // Arrange
        Long projectId = 1L;
        Member authorizedMember = Member.builder().id(1L).role(MemberRole.OWNER).build();

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER)))
                .thenReturn(authorizedMember);

        // Act
        projectService.deleteProject(projectId);

        // Assert
        verify(notificationRepository).deleteByProjectId(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void deleteProject_WhenNotAuthorized_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;

        when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER)))
                .thenThrow(new UnauthorizedAccessException("User is not authorized to delete the project"));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> projectService.deleteProject(projectId));
        verifyNoInteractions(notificationRepository, projectRepository);
    }

    @Test
    void inviteUsers_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        // Arrange
        Long projectId = 1L;
        InviteUsersRequest request = new InviteUsersRequest();

        lenient().when(memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN)))
                .thenReturn(Member.builder().id(1L).role(MemberRole.OWNER).build());
        lenient().when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.inviteUsers(projectId, request));
        verifyNoInteractions(memberRepository, notificationService);
    }
}