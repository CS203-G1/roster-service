package csd.roster.domain.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class EmployeeNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(UUID employeeId) {
        super(String.format("Could not find employee %s", employeeId));
    }
}
