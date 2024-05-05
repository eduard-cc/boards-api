package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectKeyAlreadyExistsException extends ResponseStatusException {
    public ProjectKeyAlreadyExistsException(String projectKey, Long userId) {
        super(HttpStatus.CONFLICT, "Project with KEY: " + projectKey + " already exists for user with ID: " + userId);
    }
}
