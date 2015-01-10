package com.sixbynine.infosessions.event.data;

import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;

/**
 * Created by steviekideckel on 2015-01-06.
 */
public class InfoSessionPreferencesModifiedEvent {

    private WaterlooInfoSession infoSession;
    private WaterlooInfoSessionPreferences preferences;

    public InfoSessionPreferencesModifiedEvent(WaterlooInfoSession infoSession, WaterlooInfoSessionPreferences preferences){
        this.infoSession = infoSession;
        this.preferences = preferences;
    }

    public WaterlooInfoSession getInfoSession() {
        return infoSession;
    }

    public WaterlooInfoSessionPreferences getPreferences() {
        return preferences;
    }
}
