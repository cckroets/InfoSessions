package com.sixbynine.infosessions.app;

import com.flurry.android.FlurryAgent;
import com.google.inject.Inject;
import com.sixbynine.infosessions.alarm.AlarmManager;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.net.Keys;

import roboguice.activity.RoboActionBarActivity;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public abstract class BaseActivity extends RoboActionBarActivity {

    @Inject
    AlarmManager mAlarmManager;

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Keys.API_KEY_FLURRY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainBus.get().register(this);
        mAlarmManager.cancelActiveNotifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainBus.get().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
