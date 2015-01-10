package com.sixbynine.infosessions.data;

/**
 * @author curtiskroetsch
 */
public interface ResponseHandler<T> {

    void onSuccess(T object);

    void onFailure(Exception error);
}
