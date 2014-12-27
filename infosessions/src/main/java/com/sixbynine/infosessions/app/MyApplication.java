package com.sixbynine.infosessions.app;

import android.app.Application;

/**
 * Created by stevenkideckel on 14-12-27.
 */
public class MyApplication extends Application{
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
