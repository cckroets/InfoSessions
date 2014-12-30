package com.sixbynine.infosessions.app;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.net.Keys;

import roboguice.activity.RoboActionBarActivity;


public class MainActivity extends RoboActionBarActivity {

    private static final String TAG = MainActivity.class.getName();

    private InfoSessionListFragment mInfoSessionListFragment;
    private DisplayState mDisplayState;

    private SearchView mSearchView;

    enum DisplayState{
        UNDISMISSED,
        DISMISSED,
        QUERY,
        FAVORIES
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplayState = DisplayState.UNDISMISSED;
        setContentView(R.layout.activity_main);
        mInfoSessionListFragment = new InfoSessionListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mInfoSessionListFragment)
                .commit();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final ActionBar actionBar = getSupportActionBar();
        switch(mDisplayState){
            case DISMISSED:
                menu.findItem(R.id.action_view_dismissed).setTitle(R.string.action_view_undismissed);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(R.string.dismissed_sessions);
                menu.findItem(R.id.action_search).setVisible(false);
                break;
            case UNDISMISSED:
                menu.findItem(R.id.action_view_dismissed).setTitle(R.string.action_view_dismissed);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setTitle(R.string.app_name);
                menu.findItem(R.id.action_search).setVisible(true);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
            case R.id.action_view_dismissed:
                if(mDisplayState == DisplayState.DISMISSED){
                    mDisplayState = DisplayState.UNDISMISSED;
                }else{
                    mDisplayState = DisplayState.DISMISSED;
                }
                invalidateOptionsMenu();
                mInfoSessionListFragment.updateDisplayState(mDisplayState, null);
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

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if(s.length() > 0){
                mDisplayState = DisplayState.QUERY;
            }
            if(mDisplayState == DisplayState.QUERY) {
                mInfoSessionListFragment.updateDisplayState(mDisplayState, s);
            }

            return true;
        }
    };
}
