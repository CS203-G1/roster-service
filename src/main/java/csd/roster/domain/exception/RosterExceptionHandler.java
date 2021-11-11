package csd.roster.domain.exception;

import csd.roster.domain.exception.exceptions.NoOptimalSolutionException;
import csd.roster.domain.exception.exceptions.ResourceNotFoundException;
import csd.roster.domain.exception.exceptions.RuleViolatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RosterExceptionHandler {
    final Logger logger = LoggerFactory.getLogger(RosterExceptionHandler.class);

    @ExceptionHandler({ ResourceNotFoundException.class })
    public final ResponseEntity handleNotFoundException(Exception ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity(HttpStatus.NOT_FOUND)
                .status(HttpStatus.NOT_FOUND)
                .body(errorMessage);
    }

    @ExceptionHandler({ NoOptimalSolutionException.class })
    public final void handleNoOptimalSolutionException(Exception ex, WebRequest request) {
        String errorMessage = ex.getMessage();

        logger.warn(errorMessage);
        
        // Need send email here
    }

    @ExceptionHandler({ RuleViolatedException.class })
    public final ResponseEntity handleRuleViolatedException(Exception ex, WebRequest request) {
        String errorMessage = ex.getMessage();

        logger.warn(errorMessage);

        return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorMessage);
    }
}
