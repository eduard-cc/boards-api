package fhict.boards.service;

import fhict.boards.domain.dto.NotificationResponse;
import fhict.boards.exception.NotificationNotFoundException;
import fhict.boards.exception.UnauthorizedAccessException;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.entity.Member;
import fhict.boards.repository.entity.Notification;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    private AccessToken requestAccessToken;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void getNotificationsByUserId_WhenValidInput_ShouldReturnListOfNotificationResponses() {
        // Arrange
        Long userId = 1L;

        Notification notification1 = Notification.builder().id(1L).build();
        Notification notification2 = Notification.builder().id(2L).build();

        List<Notification> notifications = List.of(notification1, notification2);

        NotificationResponse response1 = NotificationResponse.builder().id(1L).build();
        NotificationResponse response2 = NotificationResponse.builder().id(2L).build();

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(notificationRepository.findAllByReceiverUserId(eq(userId), any(Sort.class))).thenReturn(notifications);
        when(dtoMapper.mapToNotificationResponse(notification1)).thenReturn(response1);
        when(dtoMapper.mapToNotificationResponse(notification2)).thenReturn(response2);

        // Act
        List<NotificationResponse> result = notificationService.getNotificationsByUserId(userId);

        // Assert
        assertEquals(List.of(response1, response2), result);
        verify(dtoMapper, times(2)).mapToNotificationResponse(any(Notification.class));
    }

    @Test
    void getNotificationsByUserId_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long userId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId + 1);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> notificationService.getNotificationsByUserId(userId));
    }

    @Test
    void deleteNotification_WhenValidInput_ShouldDeleteNotification() {
        // Arrange
        Long userId = 1L;
        Long notificationId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);
        lenient().when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(new Notification()));

        // Act
        notificationService.deleteNotification(userId, notificationId);

        // Assert
        verify(notificationRepository).deleteById(notificationId);
    }

    @Test
    void deleteNotification_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long userId = 1L;
        Long notificationId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId + 1);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> notificationService.deleteNotification(userId, notificationId));
        verify(notificationRepository, never()).deleteById(any());
    }

    @Test
    void deleteAllNotifications_WhenValidInput_ShouldDeleteAllNotifications() {
        // Arrange
        Long userId = 1L;

        lenient().when(requestAccessToken.getUserId()).thenReturn(userId);
        doNothing().when(notificationRepository).deleteAllByReceiverUserId(userId);

        // Act
        notificationService.deleteAllNotifications(userId);

        // Assert
        verify(notificationRepository).deleteAllByReceiverUserId(userId);
    }

    @Test
    void deleteAllNotifications_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        Long userId = 1L;

        lenient().when(requestAccessToken.getUserId()).thenReturn(userId);

        // Act and Assert
        assertThrows(UnauthorizedAccessException.class, () -> notificationService.deleteAllNotifications(userId + 1));
        verify(notificationRepository, never()).deleteAllByReceiverUserId(any(Long.class));
    }

    @Test
    void toggleRead_WhenValidInput_ShouldToggleReadStatusAndSave() {
        // Arrange
        Long userId = 1L;
        Long notificationId = 1L;

        Notification notification = Notification.builder().id(notificationId).receiver(Member.builder().user(User.builder().id(userId).build()).build()).read(false).build();
        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.toggleRead(userId, notificationId);

        // Assert
        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void toggleRead_WhenNotificationNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long notificationId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotificationNotFoundException.class, () -> notificationService.toggleRead(userId, notificationId));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void toggleRead_WhenUserIdsDoNotMatch_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long userId = 1L;
        Long notificationId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId + 1);

        // Act and Assert
        assertThrows(UnauthorizedAccessException.class, () -> notificationService.toggleRead(userId, notificationId));
        verify(notificationRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createAndSendNotification_WhenValidInput_ShouldSaveAndSendMessage() {
        // Arrange
        Long receiverUserId = 2L;
        Long senderUserId = 1L;

        Notification notification = Notification.builder()
                .receiver(Member.builder().user(User.builder().id(receiverUserId).build()).build())
                .sender(Member.builder().user(User.builder().id(senderUserId).build()).build())
                .id(1L)
                .build();

        NotificationResponse notificationResponse = NotificationResponse.builder().id(1L).build();

        lenient().when(requestAccessToken.getUserId()).thenReturn(senderUserId);
        lenient().when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        lenient().when(dtoMapper.mapToNotificationResponse(notification)).thenReturn(notificationResponse);

        // Act
        notificationService.createAndSendNotification(notification);

        // Assert
        verify(notificationRepository).save(notification);
        verify(dtoMapper).mapToNotificationResponse(notification);
        verify(messagingTemplate).convertAndSendToUser((receiverUserId.toString()), "/queue/notifications", notificationResponse);
    }
}