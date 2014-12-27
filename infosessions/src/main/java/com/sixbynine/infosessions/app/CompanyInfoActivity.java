package com.sixbynine.infosessions.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

import roboguice.activity.RoboActionBarActivity;

public class CompanyInfoActivity extends RoboActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        final Fragment fragment = new CompanyInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
