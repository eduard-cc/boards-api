package fhict.boards.service;

import fhict.boards.domain.dto.IssueCreateRequest;
import fhict.boards.domain.dto.IssueResponse;
import fhict.boards.domain.dto.IssueUpdateRequest;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.NotificationType;
import fhict.boards.exception.IssueNotFoundException;
import fhict.boards.exception.MemberNotFoundException;
import fhict.boards.repository.IssueRepository;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.entity.Issue;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.Notification;
import fhict.boards.repository.entity.Project;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static fhict.boards.domain.enums.MemberRole.*;

@Service
@AllArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final DtoMapper dtoMapper;
    private final MemberService memberService;
    private final ProjectService projectService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public IssueResponse createIssue(Long projectId, IssueCreateRequest request) {
        Project project = projectService.getProjectByIdOrThrowNotFound(projectId);

        Member assignee;
        if (request.getAssigneeMemberId() == null) {
            assignee = null;
        } else {
            assignee = getMemberOrThrowNotFound(request.getAssigneeMemberId(), projectId);
        }

        Member creator = memberService.assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN, DEVELOPER));

        Optional<Long> latestKeyNumber = issueRepository.findLatestIssueNumberByProjectId(projectId);
        long keyNumber = latestKeyNumber.orElse(0L) + 1;
        String key = project.getKey() + "-" + keyNumber;

        Issue issue = Issue.builder()
                .project(project)
                .key(key)
                .title(request.getTitle())
                .description(request.getDescription())
                .assignee(assignee)
                .type(request.getType())
                .status(request.getStatus())
                .priority(request.getPriority())
                .createdOn(LocalDateTime.now())
                .dueOn(request.getDueOn())
                .createdBy(creator)
                .build();

        Issue savedIssue = issueRepository.save(issue);

        if (assignee != null && !Objects.equals(creator, assignee)) {
            Notification notification = Notification.builder()
                    .type(NotificationType.ASSIGNED_TO_ISSUE)
                    .sender(creator)
                    .receiver(assignee)
                    .project(project)
                    .issue(issue)
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();
            notificationService.createAndSendNotification(notification);
        }

        return dtoMapper.mapToIssueResponse(savedIssue);
    }

    @Override
    public List<IssueResponse> getIssuesByProjectId(Long id) {
        memberService.assertMemberIsAuthorized(id, EnumSet.of(OWNER, ADMIN, DEVELOPER, VIEWER));

        Project project = projectService.getProjectByIdOrThrowNotFound(id);

        return project.getIssues()
                .stream()
                .map(dtoMapper::mapToIssueResponse)
                .toList();
    }

    @Override
    public IssueResponse getIssueById(Long id, Long projectId) {
        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN, DEVELOPER, VIEWER));

        Issue issue = getIssueByIdOrThrowNotFound(id);

        return dtoMapper.mapToIssueResponse(issue);
    }

    @Override
    public IssueResponse updateIssue(Long issueId, Long projectId, IssueUpdateRequest request) {
        Member updater = memberService.assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN, DEVELOPER));

        Issue issue = getIssueByIdOrThrowNotFound(issueId);

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setType(request.getType());
        issue.setStatus(request.getStatus());
        issue.setPriority(request.getPriority());
        issue.setDueOn(request.getDueOn());
        issue.setUpdatedOn(LocalDateTime.now());

        if (request.getAssigneeMemberId() == null) {
            issue.setAssignee(null);
        } else {
            Member member = getMemberOrThrowNotFound(request.getAssigneeMemberId(), projectId);

            if (!Objects.equals(issue.getAssignee(), member)) {
                issue.setAssignee(member);

                if (!Objects.equals(updater, member)) {
                    Notification notification = Notification.builder()
                            .type(NotificationType.ASSIGNED_TO_ISSUE)
                            .sender(updater)
                            .receiver(member)
                            .project(issue.getProject())
                            .issue(issue)
                            .timestamp(LocalDateTime.now())
                            .read(false)
                            .build();

                    notificationService.createAndSendNotification(notification);
                }
            }
        }

        Issue updatedIssue = issueRepository.save(issue);
        return dtoMapper.mapToIssueResponse(updatedIssue);
    }

    @Override
    public IssueResponse updateStatus(Long issueId, Long projectId, IssueStatus status) {
        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN, DEVELOPER));

        Issue issue = getIssueByIdOrThrowNotFound(issueId);
        issue.setStatus(status);

        Issue updatedIssue = issueRepository.save(issue);
        return dtoMapper.mapToIssueResponse(updatedIssue);
    }

    @Override
    public void deleteIssue(Long issueId, Long projectId) {
        memberService.assertMemberIsAuthorized(projectId, EnumSet.of(OWNER, ADMIN, DEVELOPER));

        notificationRepository.deleteByIssueId(issueId);
        issueRepository.deleteById(issueId);
    }

    @Override
    public Issue getIssueByIdOrThrowNotFound(Long id) {
        Optional<Issue> issue = issueRepository.findById(id);

        if (issue.isEmpty()) {
            throw new IssueNotFoundException(id);
        }
        return issue.get();
    }

    public Member getMemberOrThrowNotFound(Long memberId, Long projectId) {
        Optional<Member> member = memberRepository.findByIdAndProjectId
                (memberId, projectId);

        if (member.isEmpty()) {
            throw new MemberNotFoundException(memberId);
        }
        return member.get();
    }
}
