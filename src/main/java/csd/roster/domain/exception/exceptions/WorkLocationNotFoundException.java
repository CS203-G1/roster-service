package csd.roster.domain.exception.exceptions;

import csd.roster.domain.model.Company;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class WorkLocationNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public WorkLocationNotFoundException(UUID departmentId, UUID workLocationId) {
        super(String.format("Could not find work location %s from department %s",
                workLocationId,
                departmentId));
    }

    public WorkLocationNotFoundException(Company company) {
        super(String.format("Work Location does not exist in company %s", company.getId()));
    }

    // Overloaded constructor
    public WorkLocationNotFoundException(UUID workLocationId) {
        super(String.format("Could not find work location %s", workLocationId));
    }

}
