package com.sixbynine.infosessions.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.database.WebData;
import com.sixbynine.infosessions.net.CompanyDataUtil;
import com.sixbynine.infosessions.net.InfoSessionUtil;
import com.sixbynine.infosessions.net.Keys;
import com.sixbynine.infosessions.object.company.Company;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<InfoSession> mInfoSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInfoSessions();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void initInfoSessions() {
        InfoSessionUtil.getInfoSessions(new InfoSessionUtil.InfoSessionsCallback() {
            @Override
            public void onSuccess(List<InfoSessionWaterlooApiDAO> infoSessions) {
                WebData.saveSessionsToDB(MainActivity.this, infoSessions);

                for (InfoSessionWaterlooApiDAO waterlooApiDAO : infoSessions) {
                    CompanyDataUtil.getCompanyData(waterlooApiDAO, new CompanyDataUtil.CompanyDataUtilCallback() {
                        @Override
                        public void onSuccess(InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO, Company crunchbaseApiDAO) {
                            if (mInfoSessions == null) {
                                mInfoSessions = new ArrayList<InfoSession>();
                            }
                            InfoSession infoSession = new InfoSession();
                            infoSession.waterlooApiDAO = infoSessionWaterlooApiDAO;
                            infoSession.companyInfo = crunchbaseApiDAO;
                            mInfoSessions.add(infoSession);

                        }

                        @Override
                        public void onFailure(Throwable e) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Throwable e) {
                //TODO: do something in case of error
                Log.d("InfoSessions", "failure: " + e.toString());
            }
        });
    }


}
