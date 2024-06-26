package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectNotFoundException extends ResponseStatusException {
    public ProjectNotFoundException(Long projectId) {
        super(HttpStatus.NOT_FOUND, "Project not found with ID: " + projectId);
    }
}
