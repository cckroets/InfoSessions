package com.sixbynine.infosessions.model;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by stevenkideckel on 14-12-29.
 */
public class WaterlooInfoSessionPreferencesMap extends HashMap<String, WaterlooInfoSessionPreferences> {

    public WaterlooInfoSessionPreferences put(WaterlooInfoSessionPreferences preferences){
        return put(preferences.getId(), preferences);
    }

    /**
     * Convenience method for get(infoSession.getId());
     * @param infoSession the info session to get the preference for
     * @return the preferences, or null if no mapping was found
     */
    public WaterlooInfoSessionPreferences get(WaterlooInfoSession infoSession){
        return get(infoSession.getId());
    }

}
