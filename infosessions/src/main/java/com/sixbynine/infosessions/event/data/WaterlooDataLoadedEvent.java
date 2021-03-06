package com.sixbynine.infosessions.event.data;

import com.sixbynine.infosessions.model.PermalinkMap;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;

/**
 * @author curtiskroetsch
 */
public final class WaterlooDataLoadedEvent {

    private final WaterlooInfoSessionCollection mData;
    private final PermalinkMap mPermalinkMap;

    public WaterlooDataLoadedEvent(WaterlooInfoSessionCollection collection, PermalinkMap map) {
        mData = collection;
        mPermalinkMap = map;
    }

    public WaterlooInfoSessionCollection getData() {
        return mData;
    }

    public PermalinkMap getPermalinkMap() {
        return mPermalinkMap;
    }
}
