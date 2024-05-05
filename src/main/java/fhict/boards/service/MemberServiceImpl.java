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
import fhict.boards.security.token.AccessToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static fhict.boards.domain.enums.MemberRole.ADMIN;
import static fhict.boards.domain.enums.MemberRole.OWNER;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final DtoMapper dtoMapper;
    private final AccessToken requestAccessToken;
    private final IssueRepository issueRepository;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;

    @Override
    public MemberResponse getMemberById(Long id) {
        Member member = getMemberByIdOrThrowNotFound(id);
        return dtoMapper.mapToMemberResponse(member);
    }

    @Override
    public List<MemberResponse> getMembersByProjectId(Long id) {
        Optional<Project> project = projectRepository.findById(id);

        if (project.isEmpty()) {
            throw new ProjectNotFoundException(id);
        }

        // project.getMembers() triggers FetchType.LAZY?
        return project.get()
                .getMembers()
                .stream()
                .map(dtoMapper::mapToMemberResponse)
                .toList();
    }

    @Override
    public void removeMember(Long id) {
        Member target = getMemberByIdOrThrowNotFound(id);

        Long projectId = target.getProject().getId();

        Long memberCount = memberRepository.countByProjectId(projectId);
        if (!Objects.equals(requestAccessToken.getUserId(), target.getUser().getId())) {
            Member remover = assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN));

            if (remover.getRole() == ADMIN) {
                if (target.getRole() == ADMIN) {
                    throw new ProjectUnauthorizedAccessException("Admins can't remove other Admins.");
                }

                if (target.getRole() == OWNER) {
                    throw new ProjectUnauthorizedAccessException("Admins can't remove the Owner.");
                }
            }
        } else {
            if (target.getRole() == OWNER && memberCount != 1L) {
                throw new ProjectUnauthorizedAccessException
                        ("Owner must reassign their role to another member before leaving the project");
            }
        }
        commentRepository.deleteByCreatedBy_Id(id);
        notificationRepository.deleteByMemberId(id);
        issueRepository.setAssigneeAndCreatedByToNull(id);
        memberRepository.deleteById(id);

        if (memberCount == 1L) {
            notificationRepository.deleteByProjectId(projectId);
            projectRepository.deleteById(projectId);
        }
    }

    @Override
    public MemberResponse updateMemberRole(Long id, MemberRoleUpdateRequest request) {
        Member target = getMemberByIdOrThrowNotFound(id);

        Member updater = assertMemberIsAuthorized(target.getProject().getId(), EnumSet.of(OWNER, ADMIN));

        if (updater.getRole() == ADMIN) {
            if (target.getRole() == ADMIN) {
                throw new ProjectUnauthorizedAccessException("Admins can't change the role of other Admins.");
            }

            if (target.getRole() == OWNER) {
                throw new ProjectUnauthorizedAccessException("Admins can't change the Owner's role.");
            }
        }

        if (updater.getRole() == OWNER && request.getRole() == OWNER) {
            updater.setRole(ADMIN);
            memberRepository.save(updater);
        } else {
            target.setRole(request.getRole());
            memberRepository.save(target);
        }

        return dtoMapper.mapToMemberResponse(target);
    }

    @Override
    public MemberResponse getCurrentMember(Long userId, Long projectId) {
        Optional<Member> member = memberRepository.findByUserIdAndProjectId(userId, projectId);

        if (member.isEmpty()) {
            throw new MemberNotFoundException();
        }
        return dtoMapper.mapToMemberResponse(member.get());
    }

    public Member getMemberByIdOrThrowNotFound(Long id) {
        Optional<Member> member = memberRepository.findById(id);

        if (member.isEmpty()) {
            throw new MemberNotFoundException(id);
        }
        return member.get();
    }

    public Member assertMemberIsAuthorized(Long projectId, EnumSet<MemberRole> rolesAllowed) {
        Optional<Member> member = memberRepository.findByUserIdAndProjectId(requestAccessToken.getUserId(), projectId);

        if (member.isEmpty()) {
            throw new MemberNotFoundException("Authenticated user is not a member of project with ID: " + projectId);
        }
        MemberRole role = member.get().getRole();

        if (!rolesAllowed.contains(role)) {
            throw new ProjectUnauthorizedAccessException(role + " is unauthorized to perform this action.");
        }
        return member.get();
    }
}
