package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.repository.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DtoMapperImpl implements DtoMapper {

    // Issue -> IssueResponse
    @Override
    public IssueResponse mapToIssueResponse(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .key(issue.getKey())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .assignee(issue.getAssignee() != null ? mapToMemberResponse(issue.getAssignee()) : null)
                .type(issue.getType())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .createdOn(issue.getCreatedOn())
                .dueOn(issue.getDueOn())
                .createdBy(issue.getCreatedBy() != null ? mapToMemberResponse(issue.getCreatedBy()) : null)
                .updatedOn(issue.getUpdatedOn())
                .build();
    }

    // Project -> ProjectResponse
    @Override
    public ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .key(project.getKey())
                .icon(project.getIcon())
                .build();
    }

    // Member -> MemberResponse
    @Override
    public MemberResponse mapToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .user(mapToUserResponse(member.getUser()))
                .role(member.getRole())
                .joinedOn(member.getJoinedOn())
                .build();
    }

    // User -> UserResponse
    @Override
    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .company(user.getCompany())
                .location(user.getLocation())
                .picture(user.getPicture())
                .build();
    }

    // Notification -> NotificationResponse
    @Override
    public NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .sender(mapToMemberResponse(notification.getSender()))
                .receiver(mapToMemberResponse(notification.getReceiver()))
                .issue(notification.getIssue() != null ? mapToIssueResponse(notification.getIssue()) : null)
                .project(notification.getProject() != null ? mapToProjectResponse(notification.getProject()) : null)
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .build();
    }

    // Comment -> CommentResponse
    @Override
    public CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .createdBy(mapToMemberResponse(comment.getCreatedBy()))
                .createdOn(comment.getCreatedOn())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .body(comment.getBody())
                .build();
    }
}
