package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.repository.entity.*;

public interface DtoMapper {
    IssueResponse mapToIssueResponse(Issue issue);
    ProjectResponse mapToProjectResponse(Project project);
    MemberResponse mapToMemberResponse(Member member);
    UserResponse mapToUserResponse(User user);
    NotificationResponse mapToNotificationResponse(Notification notification);
    CommentResponse mapToCommentResponse(Comment comment);
}
