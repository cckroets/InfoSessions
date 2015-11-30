package com.sixbynine.infosessions.event.data;

import com.sixbynine.infosessions.model.WaterlooInfoSession;

public final class InfoSessionPreferencesModifiedEvent {

    private final WaterlooInfoSession infoSession;

    public InfoSessionPreferencesModifiedEvent(WaterlooInfoSession infoSession) {
        this.infoSession = infoSession;
    }

    public WaterlooInfoSession getInfoSession() {
        return infoSession;
    }
}
