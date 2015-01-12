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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.crittercism.app.Crittercism;
import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.BaseActivity;
import com.sixbynine.infosessions.app.CompanyInfoActivity;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.event.data.InfoSessionPreferencesModifiedEvent;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.net.Keys;
import com.sixbynine.infosessions.search.SearchActivity;
import com.sixbynine.infosessions.settings.SettingsActivity;
import com.sixbynine.infosessions.ui.InfoSessionUtil;
import com.sixbynine.infosessions.ui.PagerSlidingTabStrip;
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
    private static final int VIEW_REQUEST_CODE = 2;

    ArrayList<WaterlooInfoSession> mInfoSessions;
    InfoSessionsTabsPagerAdapter mPagerAdapter;

    Handler mHandler;
    enum ActivityState{
        INITIALIZED, LOADING, LOADED
    }
    ActivityState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(this, Keys.APP_ID_CRITTERCISM);

        setSupportActionBar(mToolbar); //I used a toolbar here since I was having issues disabling the drop shadow from action bar
        mHandler = new Handler();
        changeState(ActivityState.INITIALIZED, false);

        boolean loaded = mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                changeState(ActivityState.LOADED, mState == ActivityState.LOADING);
                mInfoSessions = new ArrayList<>(object.getInfoSessions());
                mPagerAdapter = new InfoSessionsTabsPagerAdapter(getSupportFragmentManager(), mInfoSessions);
                mPager.setAdapter(mPagerAdapter);
                mTabs.setViewPager(mPager);
                ViewCompat.setElevation(mToolbar, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        8, getResources().getDisplayMetrics()));
                ViewCompat.setElevation(mTabs, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        8, getResources().getDisplayMetrics()));

                String data = getIntent().getDataString();
                if(data != null && data.length() > 4){
                    String sessionId = data.substring(data.length() - 4);
                    for(WaterlooInfoSession infoSession : mInfoSessions){
                        if(infoSession.getId().equals(sessionId)){
                            Intent intent = new Intent(MainActivity.this, CompanyInfoActivity.class);
                            intent.putExtra(CompanyInfoActivity.INFO_SESSION_KEY, infoSession);
                            startActivityForResult(intent, VIEW_REQUEST_CODE);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception error) {

            }
        });

        if(!loaded){
            changeState(ActivityState.LOADING, false);
        }
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
                mUtil.shareInfoSession(this, infoSession);
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
                startActivityForResult(intent, VIEW_REQUEST_CODE);
                break;
        }
    }

    private void updateListFragments(){
        for(InfoSessionListFragment frag : mPagerAdapter.getListFragments()){
            if(frag != null) frag.refreshData();
        }
    }

    private void changeState(ActivityState toState, boolean animate){
        switch(toState){
            case LOADING:
                mLoadingContainer.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                if(animate) {
                    ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f).setDuration(200);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float curVal = (float) animation.getAnimatedValue();
                            mLoadingContainer.setAlpha(curVal);
                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mLoadingContainer.setVisibility(View.GONE);
                        }
                    });
                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                    anim.start();
                }else{
                    mLoadingContainer.setVisibility(View.GONE);
                }
                break;
        }
        mState = toState;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case VIEW_REQUEST_CODE:
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
