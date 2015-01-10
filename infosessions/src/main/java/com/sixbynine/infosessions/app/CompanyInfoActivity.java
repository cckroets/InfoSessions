package com.sixbynine.infosessions.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.alarm.AlarmManager;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.ui.InfoSessionUtil;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_company_info)
public class CompanyInfoActivity extends BaseActivity {

    public static final String INFO_SESSION_KEY = "key-info-session";

    @Inject
    InfoSessionUtil mInfoSessionUtil;

    @Inject
    InfoSessionPreferenceManager mPreferencesManager;

    private WaterlooInfoSession mInfoSession;
    private CompanyInfoFragment mInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            mInfoSession = getIntent().getParcelableExtra(INFO_SESSION_KEY);
        }else{
            mInfoSession = savedInstanceState.getParcelable(INFO_SESSION_KEY);
        }

        mInfoFragment = CompanyInfoFragment.createInstance(mInfoSession);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mInfoFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.company_info, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        WaterlooInfoSessionPreferences prefs = mPreferencesManager.getPreferences(mInfoSession);
        menu.findItem(R.id.action_alarm).setTitle(prefs.hasAlarm()? R.string.remove_reminder : R.string.add_reminder);
        if(prefs.isFavorited()){
            menu.findItem(R.id.action_favourite).setIcon(R.drawable.ic_favorite_white);
            menu.findItem(R.id.action_favourite).setTitle(R.string.remove_favorite);
        }else{
            menu.findItem(R.id.action_favourite).setIcon(R.drawable.ic_favorite_white_outline);
            menu.findItem(R.id.action_favourite).setTitle(R.string.add_favorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.action_share:
                mInfoSessionUtil.shareInfoSession(this, mInfoSession);
                return true;
            case R.id.action_calendar:
                mInfoSessionUtil.launchCalendarIntent(this, mInfoSession);
                return true;
            case R.id.action_alarm:
                mInfoSessionUtil.doAlarmLogic(this, mInfoSession);
                invalidateOptionsMenu();
                return true;
            case R.id.action_rsvp:
                mInfoSessionUtil.rsvp(this, mInfoSession);
                return true;
            case R.id.action_favourite:
                mPreferencesManager.editPreferences(mInfoSession)
                        .toggleFavorited()
                        .commit();
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INFO_SESSION_KEY, mInfoSession);
    }
}
