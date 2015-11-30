package com.sixbynine.infosessions.data;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferencesMap;

import java.util.HashMap;
import java.util.Map;

@Singleton
public final class InfoSessionPreferenceManager {

    private static final String KEY_PREFERENCES = "KEY_PREFERENCES";

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    @Named("data")
    Gson mGson;

    WaterlooInfoSessionPreferencesMap mPreferencesMap;

    @Inject
    private InfoSessionPreferenceManager(Gson gson, PreferenceManager preferenceManager) {
        mGson = gson;
        mPreferenceManager = preferenceManager;
        loadData();
    }

    public WaterlooInfoSessionPreferences getPreferences(WaterlooInfoSession infoSession) {
        if (mPreferencesMap.get(infoSession) == null) {
            mPreferencesMap.put(new WaterlooInfoSessionPreferences(infoSession));
            saveData();
        }
        return mPreferencesMap.get(infoSession);
    }

    /**
     * @return a {@link com.sixbynine.infosessions.data.InfoSessionPreferenceManager.Editor} for the info session
     * preference
     */
    public Editor editPreferences(WaterlooInfoSession infoSession) {
        return new Editor(getPreferences(infoSession));
    }

    private void loadData() {
        final String preferencesMapJson = mPreferenceManager.getString(KEY_PREFERENCES, null);
        if (preferencesMapJson == null) {
            mPreferencesMap = new WaterlooInfoSessionPreferencesMap();
        } else {
            mPreferencesMap = mGson.fromJson(preferencesMapJson, WaterlooInfoSessionPreferencesMap.class);
        }
    }

    private void saveData() {
        mPreferenceManager.putString(KEY_PREFERENCES, mGson.toJson(mPreferencesMap));
    }

    /**
     * Class that provides editing functions for the preferences of a WaterlooInfoSession <p> Methods return the
     * instance for chaining, and changes will not be processed until the {@link #commit()} method is called
     */
    public class Editor {
        WaterlooInfoSessionPreferences preferences;

        public Editor(WaterlooInfoSessionPreferences preferences) {
            String json = mGson.toJson(preferences);
            this.preferences = mGson.fromJson(json, WaterlooInfoSessionPreferences.class); //clone the preferences
        }

        public Editor setDismissed(boolean dismissed) {
            preferences.setDismissed(dismissed);
            return this;
        }

        public Editor setFavorited(boolean favorited) {
            Map<String, String> params = new HashMap<>(1);
            params.put("session_id", preferences.getId());
            FlurryAgent.logEvent("Event " + (favorited ? "added to " : "removed from ") + "favourites", params);
            preferences.setFavorited(favorited);
            return this;
        }

        public Editor setAlarm(int minutes) {
            Map<String, String> params = new HashMap<>(1);
            params.put("session_id", preferences.getId());
            FlurryAgent.logEvent("Reminder created for info session", params);
            preferences.setAlarm(minutes);
            return this;
        }

        public Editor removeAlarm() {
            Map<String, String> params = new HashMap<>(1);
            params.put("session_id", preferences.getId());
            FlurryAgent.logEvent("Reminder removed for info session", params);
            preferences.removeAlarm();
            return this;
        }

        public Editor toggleFavorited() {
            return setFavorited(!preferences.isFavorited());
        }

        public Editor toggleDismissed() {
            return setDismissed(!preferences.isDismissed());
        }

        public WaterlooInfoSessionPreferences commit() {
            mPreferencesMap.put(preferences.getId(), preferences);
            saveData();
            return preferences;
        }
    }

}
