package com.sixbynine.infosessions.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Button;

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
public class MainActivity extends BaseActivity implements InfoSessionListFragment.Callback {
  @InjectView(R.id.toolbar)
  Toolbar mToolbar;
  @InjectView(R.id.tabs)
  PagerSlidingTabStrip mTabs;
  @InjectView(R.id.pager)
  ViewPager mPager;
  @InjectView(R.id.loading_container)
  ViewGroup mLoadingContainer;
  @InjectView(R.id.failure_container)
  ViewGroup mFailureContainer;
  @InjectView(R.id.retry_button)
  Button mRetryButton;

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

  enum ActivityState {
    INITIALIZED, LOADING, LOADED, ERROR
  }

  ActivityState mState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Crittercism.initialize(this, Keys.APP_ID_CRITTERCISM);

    setSupportActionBar(mToolbar); //I used a toolbar here since I was having issues disabling the drop shadow from action bar
    ViewCompat.setElevation(mToolbar, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        8, getResources().getDisplayMetrics()));
    ViewCompat.setElevation(mTabs, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        8, getResources().getDisplayMetrics()));
    mHandler = new Handler();
    changeState(ActivityState.INITIALIZED);

    loadListings(true);

    mRetryButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadListings(false);
      }
    });
  }

  @Override
  public void loadListings(boolean useCache) {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

    if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
      changeState(ActivityState.ERROR);
    } else {
      boolean loaded = mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
        @Override
        public void onSuccess(WaterlooInfoSessionCollection object) {
          changeState(ActivityState.LOADED);
          mInfoSessions = new ArrayList<>(object.getInfoSessions());
          mPagerAdapter = new InfoSessionsTabsPagerAdapter(getSupportFragmentManager(), mInfoSessions);
          mPager.setAdapter(mPagerAdapter);
          mTabs.setViewPager(mPager);

          String data = getIntent().getDataString();
          if (data != null && data.length() > 4) {
            String sessionId = data.substring(data.length() - 4);
            for (WaterlooInfoSession infoSession : mInfoSessions) {
              if (infoSession.getId().equals(sessionId)) {
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
          changeState(ActivityState.ERROR);
        }
      }, useCache);

      if (!loaded) {
        changeState(ActivityState.LOADING);
      }
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
    switch (item.getItemId()) {
      case R.id.action_settings:
        SettingsActivity.launchActivityForResult(this, SETTINGS_REQUEST_CODE);
        return true;
      case R.id.action_search:
        SearchActivity.launchActivityForResult(this, SEARCH_REQUEST_CODE, mInfoSessions);
        return true;
      case R.id.action_rate:
        StoreUtils.launchStoreIntent(this);
        return true;
      case R.id.action_share:
        StoreUtils.shareApp(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession) {
    switch (event) {
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

  private void updateListFragments() {
    for (InfoSessionListFragment frag : mPagerAdapter.getListFragments()) {
      if (frag != null) frag.refreshData();
    }
  }

  private void changeState(ActivityState toState) {
    switch (toState) {
      case LOADING:
        if (mPagerAdapter == null || !mPagerAdapter.setLoading(true)) {
          mLoadingContainer.setVisibility(View.VISIBLE);
        } else {
          mLoadingContainer.setVisibility(View.GONE);
        }
        mFailureContainer.setVisibility(View.GONE);
        break;
      case LOADED:
        if (mPagerAdapter != null) {
          mPagerAdapter.setLoading(false);
        }
        mFailureContainer.setVisibility(View.GONE);
        mLoadingContainer.setVisibility(View.GONE);
        break;
      case ERROR:
        mFailureContainer.setVisibility(View.VISIBLE);
        mLoadingContainer.setVisibility(View.GONE);
        break;

    }
    mState = toState;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mInfoSessions == null) {
      loadListings(true);
    } else {
      switch (requestCode) {
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
}
