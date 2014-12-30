package com.sixbynine.infosessions.data;

import android.preference.Preference;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferencesMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenkideckel on 14-12-29.
 */
@Singleton
public final class InfoSessionPreferenceManager {

    public static final String TAG = InfoSessionPreferenceManager.class.getName();
    private static final String KEY_PREFERENCES = "KEY_PREFERENCES";

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    @Named("data")
    Gson mGson;

    WaterlooInfoSessionPreferencesMap mPreferencesMap;

    @Inject
    private InfoSessionPreferenceManager(Gson gson, PreferenceManager preferenceManager){
        mGson = gson;
        mPreferenceManager = preferenceManager;
        loadData();
    }

    public WaterlooInfoSessionPreferences getPreferences(WaterlooInfoSession infoSession){
        if(mPreferencesMap.get(infoSession) == null){
            mPreferencesMap.put(new WaterlooInfoSessionPreferences(infoSession));
            saveData();
        }
        return mPreferencesMap.get(infoSession);
    }

    /**
     *
     * @param infoSession
     * @return a {@link com.sixbynine.infosessions.data.InfoSessionPreferenceManager.Editor} for the info session preference
     */
    public Editor editPreferences(WaterlooInfoSession infoSession){
        return new Editor(getPreferences(infoSession));
    }

    private void loadData(){
        final String preferencesMapJson = mPreferenceManager.getString(KEY_PREFERENCES, null);
        if(preferencesMapJson == null){
            mPreferencesMap = new WaterlooInfoSessionPreferencesMap();
        }else{
            mPreferencesMap = mGson.fromJson(preferencesMapJson, WaterlooInfoSessionPreferencesMap.class);
        }
    }

    private void saveData(){
        mPreferenceManager.putString(KEY_PREFERENCES, mGson.toJson(mPreferencesMap));
    }

    /**
     *
     * @param sessions the list of {@link com.sixbynine.infosessions.model.WaterlooInfoSession}s
     * @return a new list containing only the items of the list that are not dismissed
     */
    public List<WaterlooInfoSession> getUndismissedInfoSessions(List<WaterlooInfoSession> sessions){
        List<WaterlooInfoSession> infoSessions = new ArrayList<>(sessions);
        for(int i = infoSessions.size() - 1; i >= 0; i --){
            WaterlooInfoSessionPreferences preferences = getPreferences(infoSessions.get(i));
            if(preferences.isDismissed()){
                infoSessions.remove(i);
            }
        }
        return infoSessions;
    }

    /**
     *
     * @param sessions the list of {@link com.sixbynine.infosessions.model.WaterlooInfoSession}s
     * @return a new list containing only the items of the list that are not dismissed
     */
    public List<WaterlooInfoSession> getFavoriteInfoSessions(List<WaterlooInfoSession> sessions){
        List<WaterlooInfoSession> infoSessions = new ArrayList<>(sessions);
        for(int i = infoSessions.size() - 1; i >= 0; i --){
            WaterlooInfoSessionPreferences preferences = getPreferences(infoSessions.get(i));
            if(preferences.isDismissed()){
                infoSessions.remove(i);
            }
        }
        return infoSessions;
    }
    /**
     *
     * @param sessions the list of {@link com.sixbynine.infosessions.model.WaterlooInfoSession}s
     * @return a new list containing only the items of the list that are dismissed
     */
    public List<WaterlooInfoSession> getDismissedInfoSessions(List<WaterlooInfoSession> sessions){
        List<WaterlooInfoSession> infoSessions = new ArrayList<>(sessions);
        for(int i = infoSessions.size() - 1; i >= 0; i --){
            WaterlooInfoSessionPreferences preferences = getPreferences(infoSessions.get(i));
            if(!preferences.isDismissed()){
                infoSessions.remove(i);
            }
        }
        return infoSessions;
    }

    /**
     * Class that provides editing functions for the preferences of a WaterlooInfoSession
     * <p>
     * Methods return the instance for chaining, and changes will not be processed until the
     * {@link #commit()} method is called
     */
    public class Editor{
        WaterlooInfoSessionPreferences preferences;

        public Editor(WaterlooInfoSessionPreferences preferences){
            String json = mGson.toJson(preferences);
            this.preferences = mGson.fromJson(json, WaterlooInfoSessionPreferences.class); //clone the preferences
        }

        public Editor setDismissed(boolean dismissed){
            preferences.setDismissed(dismissed);
            return this;
        }

        public Editor setFavorited(boolean favorited){
            preferences.setFavorited(favorited);
            return this;
        }

        public Editor addAlarm(int alarmId){
            preferences.addAlarm(alarmId);
            return this;
        }

        public Editor setAlarms(List<Integer> alarms){
            preferences.setAlarmIds(alarms);
            return this;
        }

        public Editor removeAlarm(int alarmId){
            preferences.removeAlarm(alarmId);
            return this;
        }

        public Editor toggleFavorited(){
            preferences.setFavorited(!preferences.isFavorited());
            return this;
        }

        public Editor toggleDismissed(){
            preferences.setDismissed(!preferences.isDismissed());
            return this;
        }

        public void commit(){
            mPreferencesMap.put(preferences.getId(), preferences);
            saveData();
        }
    }

}
