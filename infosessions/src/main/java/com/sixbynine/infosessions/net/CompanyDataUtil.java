package com.sixbynine.infosessions.net;

import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.infosessions.object.InfoSessionCrunchbaseApiDAO;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class CompanyDataUtil {

    private static AsyncCompanyDataFetcher sAsyncCompanyDataFetcher;

    public interface CompanyDataUtilCallback {
        public void onSuccess(InfoSessionWaterlooApiDAO waterlooApiDAO, InfoSessionCrunchbaseApiDAO crunchbaseApiDAO);

        public void onFailure(Throwable e);
    }

    public static void getCompanyData(InfoSessionWaterlooApiDAO waterlooApiDAO, CompanyDataUtilCallback callback) {
        if (sAsyncCompanyDataFetcher == null || sAsyncCompanyDataFetcher.getStatus().equals(AsyncTask.Status.FINISHED)) {
            sAsyncCompanyDataFetcher = new AsyncCompanyDataFetcher(waterlooApiDAO);
            sAsyncCompanyDataFetcher.execute(callback);
        }


    }

    private static class CallProcessor implements CrunchbaseApiRestClient.Callback {
        private CompanyDataUtilCallback callback;
        private InfoSessionWaterlooApiDAO waterlooApiDAO;

        public CallProcessor(CompanyDataUtilCallback callback, InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO) {
            this.callback = callback;
            this.waterlooApiDAO = infoSessionWaterlooApiDAO;
        }

        @Override
        public void onSuccess(JSONObject obj) {
            Log.d("InfoSessions", obj.toString());
            try {
                JSONArray data = obj.getJSONArray("data");

            } catch (JSONException e) {
                callback.onFailure(e);
            }

        }

        @Override
        public void onFailure(Throwable e) {
            callback.onFailure(e);

        }
    }

    private static class AsyncCompanyDataFetcher extends AsyncTask<CompanyDataUtilCallback, Void, Void> {

        private InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO;

        public AsyncCompanyDataFetcher(InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO) {
            this.infoSessionWaterlooApiDAO = infoSessionWaterlooApiDAO;
        }

        @Override
        protected Void doInBackground(CompanyDataUtilCallback... callbacks) {
            if (callbacks.length == 0) {
                throw new IllegalArgumentException("Must provide a callback");
            } else if (callbacks[0] == null) {
                throw new IllegalArgumentException("Provided callback is null");
            }
            CompanyDataUtilCallback callback = callbacks[0];
            CrunchbaseApiRestClient.get("/organization/" + infoSessionWaterlooApiDAO.getEmployer(), null, new CallProcessor(callback, infoSessionWaterlooApiDAO));

            return null;
        }
    }
}
