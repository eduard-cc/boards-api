package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MemberAlreadyExistsException extends ResponseStatusException {
    public MemberAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, "Member with email: " + email + " already exists.");
    }
}
