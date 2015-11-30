package com.sixbynine.infosessions.alarm;

import android.support.annotation.IntDef;

import com.sixbynine.infosessions.data.PreferenceManager;

public final class NotificationPreference {

    @IntDef(flag = true, value = {VIBRATE, SOUND, LIGHTS})
    public @interface Feature {
    }

    public static final int VIBRATE = 1;
    public static final int SOUND = 2;
    public static final int LIGHTS = 4;

    @Feature
    private int flags;

    public NotificationPreference(@Feature int x) {
        flags = x;
    }

    public boolean has(@Feature int feature) {
        return (flags & feature) == feature;
    }

    public boolean hasVibrate() {
        return has(VIBRATE);
    }

    public boolean hasSound() {
        return has(SOUND);
    }

    public boolean hasLights() {
        return has(LIGHTS);
    }

    public void add(@Feature int feature) {
        flags = flags | feature;
    }

    public void remove(@Feature int feature) {
        //noinspection ResourceType
        flags = flags - (flags & feature);
    }

    public static NotificationPreference getNotificationPreference(PreferenceManager manager) {
        int pref = manager.getInt(PreferenceManager.Keys.ALERT_PREFERENCE, VIBRATE | SOUND | LIGHTS);
        //noinspection ResourceType
        return new NotificationPreference(pref);
    }

    public static void saveNotificationPreference(NotificationPreference pref, PreferenceManager manager) {
        manager.putInt(PreferenceManager.Keys.ALERT_PREFERENCE, pref.flags);
    }


}
