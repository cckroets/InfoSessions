package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;
import com.sixbynine.infosessions.net.serialization.CompanyDeserializer;
import com.sixbynine.infosessions.net.serialization.InfoSessionDeserializer;
import com.sixbynine.infosessions.net.serialization.PermalinkDeserializer;
import com.sixbynine.infosessions.model.PermalinkMap;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.company.Company;

/**
 * @author curtiskroetsch
 */
public class NetworkGsonProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapter(Company.class, new CompanyDeserializer())
                .registerTypeAdapter(WaterlooInfoSession.class, new InfoSessionDeserializer())
                .registerTypeAdapter(PermalinkMap.class, new PermalinkDeserializer())
                .create();
    }
}
