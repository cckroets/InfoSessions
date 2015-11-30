package com.sixbynine.infosessions.net;

import com.google.inject.Singleton;

import com.sixbynine.infosessions.model.PermalinkMap;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * @author curtiskroetsch
 */
public interface PermalinkAPI {

    @GET("/src/main/assets/permalinks.json")
    void getPermalinks(Callback<PermalinkMap> callback);
}
