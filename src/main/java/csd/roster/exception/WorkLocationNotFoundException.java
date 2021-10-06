package csd.roster.exception;

import csd.roster.model.Company;
import org.hibernate.jdbc.Work;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class WorkLocationNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public WorkLocationNotFoundException(UUID departmentId, UUID companyId, UUID workLocationId) {
        super(String.format("Could not find work location %s from department %s from company %s",
                workLocationId,
                departmentId,
                companyId));
    }

    public WorkLocationNotFoundException(Company company) {
        super(String.format("Work Location does not exist in company %s", company.getId()));
    }

    // Overloaded constructor
    public WorkLocationNotFoundException(UUID workLocationId) {
        super(String.format("Could not find work location %s", workLocationId));
    }

}
