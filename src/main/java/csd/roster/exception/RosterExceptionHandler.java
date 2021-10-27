package csd.roster.exception;

import csd.roster.exception.exceptions.NoOptimalSolutionException;
import csd.roster.exception.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RosterExceptionHandler {
    Logger logger = LoggerFactory.getLogger(RosterExceptionHandler.class);

    @ExceptionHandler({ ResourceNotFoundException.class })
    public final ResponseEntity handleNotFoundException(Exception ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity(HttpStatus.NOT_FOUND)
                .status(HttpStatus.NOT_FOUND)
                .body(errorMessage);
    }
}
