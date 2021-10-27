package csd.roster.exception.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtRequestNotFoundException extends ResourceNotFoundException {
    public ArtRequestNotFoundException(UUID id) {
        super("Could not find ArtRequest " + id);
    }
}
