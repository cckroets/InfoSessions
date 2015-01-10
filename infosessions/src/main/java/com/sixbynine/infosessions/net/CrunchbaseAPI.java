package com.sixbynine.infosessions.net;

import com.google.inject.Singleton;
import com.sixbynine.infosessions.model.company.Company;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author curtiskroetsch
 */
public interface CrunchbaseAPI {

    @GET("/organization/{permalink}")
    void getOrganization(@Path("permalink") String permalink, Callback<Company> callback);

}
