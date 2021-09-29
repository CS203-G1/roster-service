package csd.roster.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class RosterEmployeeNotFoundException  extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public RosterEmployeeNotFoundException(UUID rosterId, UUID employeeId) {
        super(String.format("Employee %s is not is Roster %s", employeeId, rosterId));
    }
}
