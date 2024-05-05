package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotificationNotFoundException extends ResponseStatusException {
    public NotificationNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Notification not found with ID: " + id);
    }
}
