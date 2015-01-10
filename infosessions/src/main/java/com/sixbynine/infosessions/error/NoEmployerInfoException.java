package com.sixbynine.infosessions.error;

/**
 * Created by stevenkideckel on 15-01-10.
 */
public class NoEmployerInfoException extends RuntimeException{
    public String sessionId;

    public NoEmployerInfoException(String sessionId){
        super("No Employer Info found for session " + sessionId);
    }
}
