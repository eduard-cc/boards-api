package fhict.boards.controller;

import fhict.boards.domain.dto.NotificationResponse;
import fhict.boards.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get notifications by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);

        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Delete a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notifications deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Toggle read status of a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification status toggled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{notificationId}")
    public ResponseEntity<Void> toggleRead(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
        notificationService.toggleRead(userId, notificationId);

        return ResponseEntity.noContent().build();
    }
}
