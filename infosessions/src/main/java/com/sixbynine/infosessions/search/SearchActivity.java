package com.sixbynine.infosessions.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.BaseActivity;
import com.sixbynine.infosessions.app.CompanyInfoActivity;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.home.InfoSessionListAdapter;
import com.sixbynine.infosessions.home.InfoSessionListFragment;
import com.sixbynine.infosessions.model.group.InfoSessionGroup;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.ui.InfoSessionUtil;

import java.util.ArrayList;

import roboguice.inject.ContentView;

/**
 * Created by stevenkideckel on 14-12-30.
 * TODO make sure this handles clicks correctly
 */
@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity implements InfoSessionListFragment.Callback{

    private static final String SESSIONS_KEY = "sessions";

    private ArrayList<WaterlooInfoSession> mAllSessions;

    InfoSessionListFragment mListFragment;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionUtil mUtil;

    public static void launchActivityForResult(Activity activity, int code, ArrayList<WaterlooInfoSession> sessions){
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putParcelableArrayListExtra(SESSIONS_KEY, sessions);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            extras.setClassLoader(WaterlooInfoSession.class.getClassLoader());
            mAllSessions = extras.getParcelableArrayList(SESSIONS_KEY);
        }else{
            mAllSessions = savedInstanceState.getParcelableArrayList(SESSIONS_KEY);
        }

        mListFragment = InfoSessionListFragment.newInstance(InfoSessionGroup.ALL, mAllSessions);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mListFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(mOnQueryTextListener);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.search_info_sessions));

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SESSIONS_KEY, mAllSessions);
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            final String query = s;
            WaterlooInfoSession.Filter filter = new WaterlooInfoSession.Filter(){
                @Override
                public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p, PreferenceManager m) {
                    return i.getCompanyName().toUpperCase().contains(query.toUpperCase());
                }
            };
            mListFragment.setDataset(filter.filter(mAllSessions));
            mListFragment.refreshData();

            return true;
        }
    };

    @Override
    public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession) {
        switch(event){
            case FAVORITE:
                mInfoSessionPreferenceManager.editPreferences(infoSession)
                        .toggleFavorited()
                        .commit();
                mListFragment.refreshData();
                break;
            case CALENDAR:
                mUtil.launchCalendarIntent(this, infoSession);
                break;
            case CLICK:
                Intent intent = new Intent(this, CompanyInfoActivity.class);
                intent.putExtra(CompanyInfoActivity.INFO_SESSION_KEY, infoSession);
                startActivity(intent);
                break;

        }
    }

}
