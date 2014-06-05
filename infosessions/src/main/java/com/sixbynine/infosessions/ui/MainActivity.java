package com.sixbynine.infosessions.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.net.InfoSessionUtil;
import com.sixbynine.infosessions.object.InfoSessionDAO;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<InfoSessionDAO> mInfoSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInfoSessions();
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
            public void onSuccess(List<InfoSessionDAO> infoSessions) {
                mInfoSessions = infoSessions;
            }

            @Override
            public void onFailure(Throwable e) {
                //TODO: do something in case of error
                Log.d("InfoSessions", "failure: " + e.toString());
            }
        });
    }

}
