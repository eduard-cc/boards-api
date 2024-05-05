package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
    }

    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "User not found with email: " + email);
    }
}
