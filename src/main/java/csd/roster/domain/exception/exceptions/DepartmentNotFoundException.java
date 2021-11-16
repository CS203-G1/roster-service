package csd.roster.domain.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class DepartmentNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public DepartmentNotFoundException(UUID departmentId, UUID companyId) {
        super(String.format("Could not find department %s from company %s", departmentId, companyId));
    }

    public DepartmentNotFoundException(UUID departmentId) {
        super(String.format("Could not find department %s", departmentId));
    }

}
