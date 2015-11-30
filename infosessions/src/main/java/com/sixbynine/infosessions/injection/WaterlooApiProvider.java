package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import com.sixbynine.infosessions.net.Keys;
import com.sixbynine.infosessions.net.WaterlooAPI;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * @author curtiskroetsch
 */
public class WaterlooApiProvider implements Provider<WaterlooAPI> {

    private static final String API_URL_WATERLOO = "https://api.uwaterloo.ca/v2";

    @Inject
    @Named("network")
    Gson mGson;

    @Override
    public WaterlooAPI get() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(API_URL_WATERLOO)
                .setConverter(new GsonConverter(mGson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("key", Keys.API_KEY_WATERLOO);
                    }
                }).build();
        return adapter.create(WaterlooAPI.class);
    }
}
