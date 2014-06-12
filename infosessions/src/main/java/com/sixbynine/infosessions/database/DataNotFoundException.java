package com.sixbynine.infosessions.database;

/**
 * @author curtiskroetsch
 */
public class DataNotFoundException extends Exception {

    public DataNotFoundException(String msg) {
        super(msg);
    }

    public DataNotFoundException(String msg, Throwable throwable) {
        super(msg,throwable);
    }
}
