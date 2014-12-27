package com.sixbynine.infosessions;

import android.app.Application;

/**
 * Extension of Application Class, can be used to get Application Context
 * Created by stevenkideckel on 14-12-26.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
