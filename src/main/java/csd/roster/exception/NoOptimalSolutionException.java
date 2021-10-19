package csd.roster.exception;

import java.util.UUID;

public class NoOptimalSolutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoOptimalSolutionException() {
        super("No optimal solution found, manual rostering is required");
    }
}
