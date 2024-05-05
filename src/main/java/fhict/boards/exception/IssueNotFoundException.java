package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IssueNotFoundException extends ResponseStatusException {
    public IssueNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Issue not found with ID: " + id);
    }
}
