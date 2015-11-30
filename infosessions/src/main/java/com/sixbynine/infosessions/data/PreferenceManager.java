package com.sixbynine.infosessions.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class PreferenceManager {

    private static final String PREFS_NAME = "infosessions";
    private static final int PREFS_MODE = Context.MODE_PRIVATE;

    SharedPreferences mSharedPreferences;

    @Inject
    private PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
    }

    public String getString(String key, String def) {
        return mSharedPreferences.getString(key, def);
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public int getInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }

    public long getLong(String key, long def) {
        return mSharedPreferences.getLong(key, def);
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }

    public Set<String> getStrings(String key) {
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void putStrings(String key, Set<String> strings) {
        mSharedPreferences.edit().putStringSet(key, strings).apply();
    }

    public final class Keys {
        public static final String INTERESTED_PROGRAMS = "programs-key";
        public static final String SHOW_COOP = "show-coop-tab-key";
        public static final String SHOW_GRADUATE = "show-grad-tab-key";
        public static final String SHOW_PAST = "show-past-tab-key";
        public static final String SHOW_TODAY = "show-today-tab-key";
        public static final String SHOW_REMINDERS = "show-reminders-tab-key";
        public static final String ALERT_PREFERENCE = "alert-preference";
    }
}
