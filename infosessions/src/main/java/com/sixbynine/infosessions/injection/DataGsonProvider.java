package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;

/**
 * @author curtiskroetsch
 */
public class DataGsonProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .create();
    }
}
