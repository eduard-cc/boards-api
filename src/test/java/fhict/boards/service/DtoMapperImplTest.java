package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.domain.enums.*;
import fhict.boards.repository.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DtoMapperImplTest {
    @InjectMocks
    private DtoMapperImpl dtoMapper;

    @Test
    void mapToIssueResponse_WhenCalled_ShouldReturnExpectedIssueResponse() {
        // Arrange
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setKey("IssueKey");
        issue.setTitle("IssueTitle");
        issue.setDescription("IssueDescription");
        issue.setAssignee(Member.builder().user(new User()).build());
        issue.setType(IssueType.EPIC);
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setPriority(IssuePriority.HIGH);
        issue.setCreatedOn(LocalDateTime.now());
        issue.setDueOn(LocalDate.now().plusDays(1));
        issue.setCreatedBy(Member.builder().user(new User()).build());

        // Act
        IssueResponse result = dtoMapper.mapToIssueResponse(issue);

        // Assert
        assertEquals(issue.getId(), result.getId());
        assertEquals(issue.getKey(), result.getKey());
        assertEquals(issue.getTitle(), result.getTitle());
        assertEquals(issue.getDescription(), result.getDescription());
        assertEquals(issue.getType(), result.getType());
        assertEquals(issue.getStatus(), result.getStatus());
        assertEquals(issue.getPriority(), result.getPriority());
        assertEquals(issue.getCreatedOn(), result.getCreatedOn());
        assertEquals(issue.getDueOn(), result.getDueOn());
        assertNotNull(result.getAssignee());
        assertNotNull(result.getCreatedBy());
    }

    @Test
    void mapToProjectResponse_WhenCalled_ShouldReturnExpectedProjectResponse() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("ProjectName");
        project.setKey("ProjectKey");
        project.setIcon(new byte[0]);

        // Act
        ProjectResponse result = dtoMapper.mapToProjectResponse(project);

        // Assert
        assertEquals(project.getId(), result.getId());
        assertEquals(project.getName(), result.getName());
        assertEquals(project.getKey(), result.getKey());
        assertEquals(project.getIcon(), result.getIcon());
    }

    @Test
    void mapToMemberResponse_WhenCalled_ShouldReturnExpectedMemberResponse() {
        // Arrange
        Member member = new Member();
        member.setId(1L);
        User user = new User();
        member.setUser(user);
        member.setRole(MemberRole.ADMIN);
        member.setJoinedOn(LocalDate.now());

        // Act
        MemberResponse result = dtoMapper.mapToMemberResponse(member);

        // Assert
        assertEquals(member.getId(), result.getId());
        assertNotNull(result.getUser());
        assertEquals(member.getRole(), result.getRole());
        assertEquals(member.getJoinedOn(), result.getJoinedOn());
    }

    @Test
    void mapToUserResponse_WhenCalled_ShouldReturnExpectedUserResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("UserEmail");
        user.setCompany("UserCompany");
        user.setLocation("UserLocation");
        user.setPicture(new byte[0]);

        // Act
        UserResponse result = dtoMapper.mapToUserResponse(user);

        // Assert
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getCompany(), result.getCompany());
        assertEquals(user.getLocation(), result.getLocation());
        assertEquals(user.getPicture(), result.getPicture());
    }

    @Test
    void mapToNotificationResponse_WhenCalled_ShouldReturnExpectedNotificationResponse() {
        // Arrange
        Member sender = Member.builder().id(1L).user(new User()).build();
        Member receiver = Member.builder().id(2L).user(new User()).build();
        Project project = new Project();
        project.setId(1L);
        Issue issue = new Issue();
        issue.setId(1L);
        Notification notification = Notification.builder()
                .id(1L)
                .type(NotificationType.ASSIGNED_TO_ISSUE)
                .sender(sender)
                .receiver(receiver)
                .project(project)
                .issue(issue)
                .timestamp(LocalDateTime.now())
                .read(true)
                .build();

        // Act
        NotificationResponse result = dtoMapper.mapToNotificationResponse(notification);

        // Assert
        assertEquals(notification.getId(), result.getId());
        assertEquals(notification.getType(), result.getType());
        assertNotNull(result.getSender());
        assertNotNull(result.getReceiver());
        assertNotNull(result.getProject());
        assertNotNull(result.getIssue());
        assertEquals(notification.getTimestamp(), result.getTimestamp());
        assertTrue(result.isRead());
    }

    @Test
    void mapToCommentResponse_WhenCalled_ShouldReturnExpectedCommentResponse() {
        // Arrange
        Member createdBy = Member.builder().id(1L).user(new User()).build();
        Comment comment = Comment.builder()
                .id(1L)
                .createdBy(createdBy)
                .createdOn(LocalDateTime.now())
                .lastUpdatedOn(LocalDateTime.now())
                .body("CommentBody")
                .build();

        // Act
        CommentResponse result = dtoMapper.mapToCommentResponse(comment);

        // Assert
        assertEquals(comment.getId(), result.getId());
        assertNotNull(result.getCreatedBy());
        assertEquals(comment.getCreatedOn(), result.getCreatedOn());
        assertEquals(comment.getLastUpdatedOn(), result.getLastUpdatedOn());
        assertEquals(comment.getBody(), result.getBody());
    }
}