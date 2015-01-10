package com.sixbynine.infosessions.event.data;

import com.sixbynine.infosessions.model.PermalinkMap;

/**
 * @author curtiskroetsch
 */
public final class PermalinksLoadedEvent {

    private PermalinkMap mPermalinkMap;

    public PermalinksLoadedEvent(PermalinkMap permalinkMap) {
        mPermalinkMap = permalinkMap;
    }

    public PermalinkMap getPermalinks() {
        return mPermalinkMap;
    }
}
