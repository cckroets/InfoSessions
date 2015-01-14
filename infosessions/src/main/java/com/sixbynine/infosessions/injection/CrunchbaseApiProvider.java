package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.sixbynine.infosessions.net.CrunchbaseAPI;
import com.sixbynine.infosessions.net.Keys;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * @author curtiskroetsch
 */
public class CrunchbaseApiProvider implements Provider<CrunchbaseAPI> {

    private static final String API_URL_CRUNCHBASE = "https://api.crunchbase.com/v/2";

    @Inject
    @Named("network")
    Gson mGson;

    @Override
    public CrunchbaseAPI get() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(API_URL_CRUNCHBASE)
                .setConverter(new GsonConverter(mGson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("user_key", Keys.API_KEY_CRUNCHBASE);
                    }
                }).build();

        return adapter.create(CrunchbaseAPI.class);
    }
}
