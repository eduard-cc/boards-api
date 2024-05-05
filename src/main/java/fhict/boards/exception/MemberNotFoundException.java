package fhict.boards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MemberNotFoundException extends ResponseStatusException {
    public MemberNotFoundException(Long memberId) {
        super(HttpStatus.NOT_FOUND, "Member not found with ID: " + memberId);
    }
    public MemberNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Member not found.");
    }
    public MemberNotFoundException(String errorCause) {
        super(HttpStatus.NOT_FOUND, errorCause);
    }
}
