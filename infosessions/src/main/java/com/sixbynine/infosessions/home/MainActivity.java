package com.sixbynine.infosessions.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.BaseActivity;
import com.sixbynine.infosessions.app.CompanyInfoActivity;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.search.SearchActivity;
import com.sixbynine.infosessions.settings.SettingsActivity;
import com.sixbynine.infosessions.ui.InfoSessionUtil;
import com.sixbynine.infosessions.ui.PagerSlidingTabStrip;
import com.sixbynine.infosessions.util.StoreUtils;

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

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionManager mInfoSessionManager;

    private static final int SEARCH_REQUEST_CODE = 0;
    private static final int SETTINGS_REQUEST_CODE = 1;


    ArrayList<WaterlooInfoSession> mInfoSessions;
    InfoSessionsTabsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar); //I used a toolbar here since I was having issues disabling the drop shadow from action bar

        mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
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
    public void onAlarmClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(this, infoSession.getCompanyName() + " alarm", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarClicked(WaterlooInfoSession infoSession) {
        InfoSessionUtil.launchCalendarIntent(this, infoSession);
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
        Intent intent = new Intent(this, CompanyInfoActivity.class);
        intent.putExtra(CompanyInfoActivity.INFO_SESSION_KEY, infoSession);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case SEARCH_REQUEST_CODE:
                updateListFragments(); //if the user favorites a session while searching, that should be reflected when they return
                break;
            case SETTINGS_REQUEST_CODE:
                mPagerAdapter.refreshData();
                mTabs.notifyDataSetChanged();
                updateListFragments();
                break;
        }
    }
}
