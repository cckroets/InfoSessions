package com.sixbynine.infosessions.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_company_info)
public class CompanyInfoActivity extends RoboActionBarActivity {

    public static final String INFO_SESSION_KEY = "key-info-session";

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INFO_SESSION_KEY, mInfoSession);
    }
}
