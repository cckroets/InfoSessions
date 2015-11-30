package com.sixbynine.infosessions.util;

import android.util.Log;

import com.crittercism.app.Crittercism;
import com.sixbynine.infosessions.BuildConfig;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public class Logger {

    private static final String TAG = "InfoSessions";

    public static void d(String message, Object... formatArgs) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format(message, formatArgs));
        }
    }

    public static void i(String message, Object... formatArgs) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format(message, formatArgs));
        }
    }

    public static void v(String message, Object... formatArgs) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, String.format(message, formatArgs));
        }
    }

    public static void w(String message, Object... formatArgs) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, String.format(message, formatArgs));
        }
    }

    public static void e(String message, Object... formatArgs) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, String.format(message, formatArgs));
        }
    }

    public static void leaveBreadcrumb(String message) {
        Logger.d(message);
        Crittercism.leaveBreadcrumb(message);
    }

    public static void logHandledException(Throwable e) {
        Logger.e(e.getMessage());
        Crittercism.logHandledException(e);
    }
}
