package com.sixbynine.infosessions.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public final class WaterlooInfoSessionCollection {

    @SerializedName("data")
    List<WaterlooInfoSession> mInfoSessions;

    private WaterlooInfoSessionCollection() {

    }

    public List<WaterlooInfoSession> getInfoSessions() {
        return mInfoSessions;
    }
}
