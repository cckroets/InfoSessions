package com.sixbynine.infosessions.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
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

    public Set<String> getStrings(String key) {
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void putStrings(String key, Set<String> strings) {
        mSharedPreferences.edit().putStringSet(key, strings).apply();
    }
}
