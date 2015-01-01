package com.sixbynine.infosessions.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;
import com.sixbynine.infosessions.model.group.InfoSessionGroup;
import com.sixbynine.infosessions.model.group.InfoSessionGroupSerializer;

/**
 * Created by stevenkideckel on 14-12-31.
 */
public class InfoSessionGroupProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapter(InfoSessionGroup.class, new InfoSessionGroupSerializer())
                .create();
    }
}
