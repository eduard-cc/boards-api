package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedAccessException extends ResponseStatusException {
    public UnauthorizedAccessException(String errorCause) {
        super(HttpStatus.FORBIDDEN, errorCause);
    }
}
