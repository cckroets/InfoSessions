package com.sixbynine.infosessions.home;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.BaseActivity;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.search.SearchActivity;
import com.sixbynine.infosessions.ui.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements InfoSessionListFragment.Callback{
    @InjectView(R.id.extended_action_bar)
    ViewGroup mExtendedActionBar;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager)
    ViewPager mPager;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionManager mInfoSessionManager;

    private static final String TAG = MainActivity.class.getName();

    ArrayList<WaterlooInfoSession> mInfoSessions;
    InfoSessionsTabsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);

        mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                mInfoSessions = new ArrayList<>(object.getInfoSessions());
                mPagerAdapter = new InfoSessionsTabsPagerAdapter(getSupportFragmentManager(), mInfoSessions);
                mPager.setAdapter(mPagerAdapter);
                mTabs.setViewPager(mPager);
                ViewCompat.setElevation(mExtendedActionBar, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        4, getResources().getDisplayMetrics()));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                SearchActivity.launchActivity(this, mInfoSessions);
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
    public void onFavoriteClicked(WaterlooInfoSession infoSession) {
        mInfoSessionPreferenceManager.editPreferences(infoSession)
                .toggleFavorited()
                .commit();
        updateListFragments();
    }

    @Override
    public void onShareClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(this, infoSession.getCompanyName() + " shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(this, infoSession.getCompanyName() + " timer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(WaterlooInfoSession infoSession) {
        mInfoSessionPreferenceManager.editPreferences(infoSession)
                .toggleDismissed()
                .commit();
        updateListFragments();
    }

    private void updateListFragments(){
        for(InfoSessionListFragment frag : mPagerAdapter.getListFragments()){
            if(frag != null) frag.refreshData();
        }
    }

    @Override
    public void onInfoSessionClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(this, infoSession.getCompanyName() + " clicked", Toast.LENGTH_SHORT).show();
    }



}