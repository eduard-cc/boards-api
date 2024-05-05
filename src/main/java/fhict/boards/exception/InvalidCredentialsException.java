package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCredentialsException extends ResponseStatusException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    public InvalidCredentialsException(String errorMessage) {
        super(HttpStatus.UNAUTHORIZED, errorMessage);
    }
}
