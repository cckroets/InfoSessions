package com.sixbynine.infosessions.ui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;

public class CompanyInfoActivity extends ActionBarActivity implements CompanyInfoFragment.Callback {

    private InfoSession mInfoSession;
    private CompanyInfoFragment mInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        if (savedInstanceState == null) {
            mInfoSession = getIntent().getParcelableExtra("infoSession");
        } else {
            mInfoSession = savedInstanceState.getParcelable("infoSession");
        }

        mInfoFragment = new CompanyInfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mInfoFragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("infoSession", mInfoSession);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.company_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_favourite) {
            // Handle the favourite button inside of the fragment
            return false;
        }
        return super.onOptionsItemSelected(item);

    }



    @Override
    public InfoSession getSelectedInfoSession() {
        return mInfoSession;
    }
}
