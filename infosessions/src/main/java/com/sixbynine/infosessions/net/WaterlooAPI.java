package com.sixbynine.infosessions.net;

import com.google.inject.Singleton;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * @author curtiskroetsch
 */
public interface WaterlooAPI {

    @GET("/resources/infosessions.json")
    void getInfoSessions(Callback<WaterlooInfoSessionCollection> callback);
}
