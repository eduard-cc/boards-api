package fhict.boards.controller;

import fhict.boards.domain.dto.NotificationResponse;
import fhict.boards.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);

        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{notificationId}")
    public ResponseEntity<Void> toggleRead(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.toggleRead(userId, notificationId);

        return ResponseEntity.noContent().build();
    }
}
