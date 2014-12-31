package com.sixbynine.infosessions.app;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.net.Keys;

import roboguice.activity.RoboActionBarActivity;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public abstract class BaseActivity extends RoboActionBarActivity{

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Keys.API_KEY_FLURRY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
