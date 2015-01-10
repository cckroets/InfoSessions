package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.sixbynine.infosessions.net.PermalinkAPI;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * @author curtiskroetsch
 */
public class GithubApiProvider implements Provider<PermalinkAPI> {

    private static final String API_URL_GITHUB = "https://raw.githubusercontent.com/cckroets/InfoSessions/b7c19f822854d3865355a92f198f2a1b847b93dd/infosessions";

    @Inject
    @Named("network")
    Gson mGson;

    @Override
    public PermalinkAPI get() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(API_URL_GITHUB)
                .setConverter(new GsonConverter(mGson))
                .build();
        return adapter.create(PermalinkAPI.class);
    }
}
