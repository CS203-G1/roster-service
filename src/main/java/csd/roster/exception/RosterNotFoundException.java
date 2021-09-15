package csd.roster.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class RosterNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public RosterNotFoundException(UUID rosterId) {
        super(String.format("Unable to find roster %s", rosterId));
    }
}