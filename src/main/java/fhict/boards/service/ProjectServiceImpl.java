package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.domain.enums.MemberRole;
import fhict.boards.domain.enums.NotificationType;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AccessToken requestAccessToken;
    private final DtoMapper dtoMapper;
    private final MemberService memberService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public ProjectResponse createProject(ProjectCreateRequest request, MultipartFile icon) throws IOException {
        handleDuplicateProject(request.getName(), request.getKey(), null);

        Project project = Project.builder()
                .name(request.getName())
                .key(request.getKey())
                .icon(icon != null ? icon.getBytes() : null)
                .build();

        List<Member> members = mapMembersFromRequest(request.getMembers(), project);
        project.setMembers(members);

        Project savedProject = projectRepository.save(project);

        // Send notification to all added members apart from creator
        Member creator = null;
        for (Member member : members) {
            if (member.getRole() == MemberRole.OWNER) {
                creator = member;
            } else {
                Notification notification = Notification.builder()
                        .type(NotificationType.ADDED_TO_PROJECT)
                        .sender(creator)
                        .receiver(member)
                        .project(project)
                        .timestamp(LocalDateTime.now())
                        .read(false)
                        .build();

                notificationService.createAndSendNotification(notification);
            }
        }
        return dtoMapper.mapToProjectResponse(savedProject);
    }

    @Override
    public List<MemberResponse> inviteUsers(Long id, InviteUsersRequest request) {
        Project project = getProjectByIdOrThrowNotFound(id);

        Member inviter = memberService.assertMemberIsAuthorized(project.getId(), EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN));

        List<Member> members = mapMembersFromRequest(request.getMembers(), project);

        for (Member member: members) {
            memberRepository.save(member);

            Notification notification = Notification.builder()
                    .type(NotificationType.ADDED_TO_PROJECT)
                    .sender(inviter)
                    .receiver(member)
                    .project(project)
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();

            notificationService.createAndSendNotification(notification);
        }

        return members
                .stream()
                .map(dtoMapper::mapToMemberResponse)
                .toList();
    }

    private List<Member> mapMembersFromRequest(List<MemberRequest> request, Project project) {
        return request
                .stream()
                .map(member -> {
                    if (project.getMembers() != null) {

                        if (member.getRole() == MemberRole.OWNER) {
                            throw new ProjectUnauthorizedAccessException
                                    ("Member with email: " + member.getEmail() + " can't be invited as Owner.");
                        }

                        for (Member projectMember : project.getMembers()) {
                            if (Objects.equals(projectMember.getUser().getEmail(), member.getEmail())) {
                                throw new MemberAlreadyExistsException(member.getEmail());
                            }
                        }
                    }

                    Optional<User> user = userRepository.findByEmail(member.getEmail());

                    if (user.isEmpty()) {
                        throw new UserNotFoundException(member.getEmail());
                    }

                    return Member.builder()
                            .user(user.get())
                            .role(member.getRole())
                            .project(project)
                            .joinedOn(LocalDate.now())
                            .build();
                })
                .toList();
    }

    @Override
    public List<ProjectResponse> getProjectsByUserId(Long userId) {
        if (!Objects.equals(requestAccessToken.getUserId(), userId)) {
            throw new UnauthorizedAccessException("Authenticated user is only authorized to get their own projects.");
        }

        List<Project> projects = getProjectByUserIdOrThrowNotFound(userId);

        return projects
                .stream()
                .map(dtoMapper::mapToProjectResponse)
                .toList();
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = getProjectByIdOrThrowNotFound(id);

        List<Project> projectsOfUser = getProjectByUserIdOrThrowNotFound(requestAccessToken.getUserId());

        if (!projectsOfUser.contains(project)) {
            throw new ProjectNotFoundException(id);
        }

        return dtoMapper.mapToProjectResponse(project);
    }

    @Override
    public byte[] updateProjectIcon(Long projectId, MultipartFile file) throws IOException {
        Project project = getProjectByIdOrThrowNotFound(projectId);

        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN));

        project.setIcon(file.getBytes());
        projectRepository.save(project);

        return project.getIcon();
    }

    @Override
    public void deleteProjectIcon(Long projectId) {
        Project project = getProjectByIdOrThrowNotFound(projectId);

        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN));

        project.setIcon(null);
        projectRepository.save(project);
    }

    @Override
    public ProjectResponse updateProjectDetails(Long projectId, ProjectUpdateRequest request) {
        handleDuplicateProject(request.getName(), request.getKey(), projectId);

        Project project = getProjectByIdOrThrowNotFound(projectId);

        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(MemberRole.OWNER, MemberRole.ADMIN));

        project.setName(request.getName());
        project.setKey(request.getKey());

        projectRepository.save(project);

        return dtoMapper.mapToProjectResponse(project);
    }

    @Override
    public void deleteProject(Long id) {
        memberService.assertMemberIsAuthorized(id, EnumSet.of(MemberRole.OWNER));

        notificationRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
    }

    private void handleDuplicateProject(String name, String key, Long projectId) {
        Optional<Project> project;
        if (projectId == null) {
            project = projectRepository.findFirstByNameOrKeyAndMembers_User_Id
                    (name, key, requestAccessToken.getUserId());
        } else {
            project = projectRepository.findFirstByNameOrKeyAndMembers_User_IdAndIdNot
                    (name, key, requestAccessToken.getUserId(), projectId);
        }

        if (project.isEmpty()) {
            return;
        }

        if (Objects.equals(project.get().getName(), name)) {
            throw new ProjectNameAlreadyExistsException(name, requestAccessToken.getUserId());
        }

        if (Objects.equals(project.get().getKey(), key)) {
            throw new ProjectKeyAlreadyExistsException(key, requestAccessToken.getUserId());
        }
    }

    @Override
    public Project getProjectByIdOrThrowNotFound(Long id) {
        Optional<Project> project = projectRepository.findById(id);

        if (project.isEmpty()) {
            throw new ProjectNotFoundException(id);
        }
        return project.get();
    }

    List<Project> getProjectByUserIdOrThrowNotFound(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        List<Member> members = memberRepository.findByUserId(userId);

        return members.stream()
                .map(Member::getProject)
                .toList();
    }
}
