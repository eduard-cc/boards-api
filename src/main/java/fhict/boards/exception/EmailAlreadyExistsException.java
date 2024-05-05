package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailAlreadyExistsException extends ResponseStatusException {
    public EmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Email already exists");
    }

    public EmailAlreadyExistsException(String errorMessage) {
        super(HttpStatus.CONFLICT, errorMessage);
    }
}
