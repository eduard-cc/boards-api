package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectNameAlreadyExistsException extends ResponseStatusException {
    public ProjectNameAlreadyExistsException(String projectName, Long userId) {
        super(HttpStatus.CONFLICT, "Project called " + projectName + " already exists for user with ID: " + userId);
    }
}
