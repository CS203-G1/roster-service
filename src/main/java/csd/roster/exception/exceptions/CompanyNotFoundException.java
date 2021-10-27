package csd.roster.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class CompanyNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public CompanyNotFoundException(UUID id) {
        super("Could not find company " + id);
    }

}
