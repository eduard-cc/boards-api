package fhict.boards.service;

import fhict.boards.domain.dto.NotificationResponse;
import fhict.boards.exception.NotificationNotFoundException;
import fhict.boards.exception.UnauthorizedAccessException;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.entity.Notification;
import fhict.boards.security.token.AccessToken;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AccessToken requestAccessToken;
    private final NotificationRepository notificationRepository;
    private final DtoMapper dtoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<NotificationResponse> getNotificationsByUserId(Long id) {
        if (!Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is only authorized to get their own notifications.");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        List<Notification> notifications = notificationRepository.findAllByReceiverUserId(id, sort);

        return notifications
                .stream()
                .map(dtoMapper::mapToNotificationResponse)
                .toList();
    }

    @Override
    public void deleteNotification(Long userId, Long notificationId) {
        if (!Objects.equals(requestAccessToken.getUserId(), userId)) {
            throw new UnauthorizedAccessException
                    ("Authenticated user is only authorized to delete their own notification.");
        }
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public void deleteAllNotifications(Long userId) {
        if (!Objects.equals(requestAccessToken.getUserId(), userId)) {
            throw new UnauthorizedAccessException
                    ("Authenticated user is only authorized to delete their own notifications.");
        }
        notificationRepository.deleteAllByReceiverUserId(userId);
    }

    @Override
    public void createAndSendNotification(Notification notification) {
        notificationRepository.save(notification);
        String userId = notification.getReceiver().getUser().getId().toString();
        NotificationResponse notificationResponse = dtoMapper.mapToNotificationResponse(notification);
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notificationResponse);
    }

    @Override
    public void toggleRead(Long userId, Long notificationId) {
        if (!Objects.equals(requestAccessToken.getUserId(), userId)) {
            throw new UnauthorizedAccessException
                    ("Authenticated user is only authorized to mark their own notification as read.");
        }
        Notification notification = getNotificationByIdOrThrowNotFound(notificationId);
        notification.setRead(!notification.isRead());

        notificationRepository.save(notification);
    }

    private Notification getNotificationByIdOrThrowNotFound(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isEmpty()) {
            throw new NotificationNotFoundException(id);
        }
        return notification.get();
    }
}
