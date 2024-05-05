package fhict.boards.service;

import fhict.boards.domain.dto.NotificationResponse;
import fhict.boards.repository.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsByUserId(Long id);
    void deleteNotification(Long userId, Long notificationId);
    void deleteAllNotifications(Long userId);
    void createAndSendNotification(Notification notification);
    void toggleRead(Long userId, Long notificationId);
}
