package com.sixbynine.infosessions.injection;

import android.app.Application;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import com.sixbynine.infosessions.net.CrunchbaseAPI;
import com.sixbynine.infosessions.net.PermalinkAPI;
import com.sixbynine.infosessions.net.WaterlooAPI;

/**
 * @author curtiskroetsch
 */
public class InfoSessionsModule extends AbstractModule {


    public InfoSessionsModule(Application app) {
    }

    @Override
    protected void configure() {
        bind(Gson.class).annotatedWith(Names.named("network")).toProvider(NetworkGsonProvider.class).in(Singleton.class);
        bind(Gson.class).annotatedWith(Names.named("data")).toProvider(DataGsonProvider.class).in(Singleton.class);
        bind(CrunchbaseAPI.class).toProvider(CrunchbaseApiProvider.class).in(Singleton.class);
        bind(WaterlooAPI.class).toProvider(WaterlooApiProvider.class).in(Singleton.class);
        bind(PermalinkAPI.class).toProvider(GithubApiProvider.class).in(Singleton.class);
    }

}
