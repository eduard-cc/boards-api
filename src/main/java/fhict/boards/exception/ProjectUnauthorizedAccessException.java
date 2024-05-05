package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectUnauthorizedAccessException extends ResponseStatusException {
    public ProjectUnauthorizedAccessException(String errorCause) {
        super(HttpStatus.FORBIDDEN, errorCause);
    }
}
