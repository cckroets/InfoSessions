package com.sixbynine.infosessions.error;

public class NoEmployerInfoException extends RuntimeException {

    public NoEmployerInfoException(String sessionId) {
        super("No Employer Info found for session " + sessionId);
    }
}
