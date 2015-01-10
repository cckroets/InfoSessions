package com.sixbynine.infosessions.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.alarm.AlarmManager;
import com.sixbynine.infosessions.app.BaseActivity;
import com.sixbynine.infosessions.app.CompanyInfoActivity;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.event.data.CompanyLoadedEvent;
import com.sixbynine.infosessions.event.data.InfoSessionPreferencesModifiedEvent;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.net.Keys;
import com.sixbynine.infosessions.search.SearchActivity;
import com.sixbynine.infosessions.settings.SettingsActivity;
import com.sixbynine.infosessions.ui.InfoSessionUtil;
import com.sixbynine.infosessions.ui.PagerSlidingTabStrip;
import com.sixbynine.infosessions.util.Logger;
import com.sixbynine.infosessions.util.StoreUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements InfoSessionListFragment.Callback{
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager)
    ViewPager mPager;
    @InjectView(R.id.loading_container)
    ViewGroup mLoadingContainer;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionManager mInfoSessionManager;

    @Inject
    InfoSessionUtil mUtil;

    private static final int SEARCH_REQUEST_CODE = 0;
    private static final int SETTINGS_REQUEST_CODE = 1;


    ArrayList<WaterlooInfoSession> mInfoSessions;
    InfoSessionsTabsPagerAdapter mPagerAdapter;

    Handler mHandler;
    enum ActivityState{
        INITIALIZED, LOADING, LOADED
    }
    ActivityState mState;
    private Runnable mLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            animateStateChange(ActivityState.LOADING);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(this, Keys.APP_ID_CRITTERCISM);
        setSupportActionBar(mToolbar); //I used a toolbar here since I was having issues disabling the drop shadow from action bar
        mHandler = new Handler();
        mHandler.postDelayed(mLoadingRunnable, 10);
        animateStateChange(ActivityState.INITIALIZED);

        mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                mHandler.removeCallbacks(mLoadingRunnable);
                animateStateChange(ActivityState.LOADED);
                mInfoSessions = new ArrayList<>(object.getInfoSessions());
                mPagerAdapter = new InfoSessionsTabsPagerAdapter(getSupportFragmentManager(), mInfoSessions);
                mPager.setAdapter(mPagerAdapter);
                mTabs.setViewPager(mPager);
                ViewCompat.setElevation(mToolbar, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        8, getResources().getDisplayMetrics()));
                ViewCompat.setElevation(mTabs, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        8, getResources().getDisplayMetrics()));
            }

            @Override
            public void onFailure(Exception error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Subscribe
    public void onInfoSessionModified(InfoSessionPreferencesModifiedEvent event) {
        updateListFragments();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                SettingsActivity.launchActivityForResult(this, SETTINGS_REQUEST_CODE);
                return true;
            case R.id.action_search:
                SearchActivity.launchActivityForResult(this, SEARCH_REQUEST_CODE, mInfoSessions);
                return true;
            case R.id.action_rate:
                startActivity(StoreUtils.getStoreIntent());
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession) {
        switch(event){
            case FAVORITE:
                mInfoSessionPreferenceManager.editPreferences(infoSession)
                        .toggleFavorited()
                        .commit();
                updateListFragments();
                break;
            case CALENDAR:
                mUtil.launchCalendarIntent(this, infoSession);
                break;
            case SHARE:
                Toast.makeText(this, infoSession.getCompanyName() + " shared", Toast.LENGTH_SHORT).show();
                break;
            case ALARM:
                mUtil.doAlarmLogic(this, infoSession);
                break;
            case DISMISS:
                mInfoSessionPreferenceManager.editPreferences(infoSession)
                        .toggleDismissed()
                        .commit();
                updateListFragments();
                break;
            case CLICK:
                Intent intent = new Intent(this, CompanyInfoActivity.class);
                intent.putExtra(CompanyInfoActivity.INFO_SESSION_KEY, infoSession);
                startActivity(intent);
                break;
        }
    }

    private void updateListFragments(){
        for(InfoSessionListFragment frag : mPagerAdapter.getListFragments()){
            if(frag != null) frag.refreshData();
        }
    }

    private void animateStateChange(ActivityState toState){
        switch(toState){
            case LOADING:
                mTabs.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                mTabs.setVisibility(View.VISIBLE);
                ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f).setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float curVal = (float) animation.getAnimatedValue();
                        mLoadingContainer.setAlpha(1f - curVal);
                        ViewGroup.LayoutParams params = mTabs.getLayoutParams();
                        params.height = (int) (curVal * mToolbar.getHeight());
                        mTabs.setLayoutParams(params);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ViewGroup.LayoutParams params = mTabs.getLayoutParams();
                        params.height = mToolbar.getHeight();
                        mTabs.setLayoutParams(params);
                        mLoadingContainer.setVisibility(View.GONE);
                    }
                });
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                break;
        }
        mState = toState;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case SEARCH_REQUEST_CODE:
                updateListFragments(); //if the user favorites a session while searching, that should be reflected when they return
                break;
            case SETTINGS_REQUEST_CODE:
                mPagerAdapter = new InfoSessionsTabsPagerAdapter(getSupportFragmentManager(), mInfoSessions);
                mPager.setAdapter(mPagerAdapter);
                mTabs.notifyDataSetChanged();
                updateListFragments();
                break;
        }
    }
}
