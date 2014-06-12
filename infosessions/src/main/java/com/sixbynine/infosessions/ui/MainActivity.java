package com.sixbynine.infosessions.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.database.DataNotFoundException;
import com.sixbynine.infosessions.database.WebData;
import com.sixbynine.infosessions.net.CompanyDataUtil;
import com.sixbynine.infosessions.net.InfoSessionUtil;
import com.sixbynine.infosessions.net.Keys;
import com.sixbynine.infosessions.object.company.Company;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements InfoSessionListFragment.Callback {

    private ArrayList<InfoSession> mInfoSessions;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInfoSessions();
        setupFragments();
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

    /**
     * Fill in company info for a given InfoSession
     *
     * @param infoSession The session to fill-in company info for
     */
    private void fillCompanyInfo(final InfoSession infoSession) {

        if (infoSession.waterlooApiDAO == null) {
            throw new IllegalArgumentException("Info Session must have Waterloo API filled in");
        }
        try {
            // First try to get the company info from the database
            WebData.get(getApplicationContext()).fillCompanyInfo(infoSession);
            Log.d("InfoSessions", infoSession.companyInfo.getName() + " data loaded from db");
        } catch (DataNotFoundException e) {
            // If the database does not have the data, load it from the web
            CompanyDataUtil.getCompanyData(infoSession.waterlooApiDAO,
                    new CompanyDataUtil.CompanyDataUtilCallback() {
                @Override
                public void onSuccess(InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO,
                                      Company crunchbaseApiDAO) {
                    infoSession.companyInfo = crunchbaseApiDAO;
                    WebData.saveCompanyInfoToDB(getApplicationContext(), infoSession);
                    Log.d("InfoSessions", crunchbaseApiDAO.getName() + " data loaded from web");
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e("InfoSessions", "Did not load companyInfo: " +
                            infoSession.waterlooApiDAO.getEmployer());
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Setup the InfoSessionListFragment
     */
    private void setupFragments() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("InfoSessions", "Loading fragment");
                mContent = new InfoSessionListFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList("infoSessions", mInfoSessions);
                mContent.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, mContent);
                transaction.commit();
            }
        });
    }

    /**
     * Initialize Info Sessions. Mainly just fills in mInfoSessions from either the
     * web or the stored database
     */
    private void initInfoSessions() {
        try {
            mInfoSessions = (ArrayList<InfoSession>) WebData.readInfoSessionsFromDB(getApplicationContext());
            for (InfoSession session : mInfoSessions) {
                fillCompanyInfo(session);
            }
        } catch (DataNotFoundException e) {

            InfoSessionUtil.getInfoSessions(new InfoSessionUtil.InfoSessionsCallback() {
                @Override
                public void onSuccess(final ArrayList<InfoSessionWaterlooApiDAO> infoSessions) {
                    Log.d("InfoSessions", "init info sessions succeeded");
                    WebData.saveSessionsToDB(MainActivity.this, infoSessions);

                    for (InfoSessionWaterlooApiDAO waterlooApiDAO : infoSessions) {
                        if (mInfoSessions == null) {
                            mInfoSessions = new ArrayList<InfoSession>();
                        }
                        final InfoSession infoSession = new InfoSession();
                        infoSession.waterlooApiDAO = waterlooApiDAO;
                        infoSession.companyInfo = null;
                        fillCompanyInfo(infoSession);
                        mInfoSessions.add(infoSession);
                    }
                    setupFragments();
                }

                @Override
                public void onFailure(Throwable e) {
                    //TODO: do something in case of error
                    Log.d("InfoSessions", "failure: " + e.toString());
                    e.printStackTrace();
                }
            });
        }
    }


    @Override
    public void onInfoSessionClicked(InfoSession infoSession) {
        //TODO: They clicked on an Info Session! Yay! Now what?
    }
}
