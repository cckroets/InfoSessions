package com.sixbynine.infosessions.net;

import android.os.AsyncTask;

import com.sixbynine.infosessions.object.InfoSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by stevenkideckel on 2014-07-29.
 */
public class PermalinkFetcher {

    public static final String PERMALINK_FILE = "https://raw.githubusercontent.com/cckroets/InfoSessions/b7c19f822854d3865355a92f198f2a1b847b93dd/infosessions/src/main/assets/permalinks.json";

    public interface Callback{
        public void onSuccess(InfoSession infoSession, String permalink);
        public void onFailure(Throwable e);
    }

    private static String getWebPage(String url) throws IllegalStateException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse execute = client.execute(httpGet);
        InputStream content = execute.getEntity().getContent();

        BufferedReader buffer = new BufferedReader(
                new InputStreamReader(content));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = buffer.readLine()) != null) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void getPermalink(InfoSession infoSession, Callback callback){
        if(callback == null){
            throw new IllegalArgumentException("Callback cannot be null!");
        }else if(infoSession == null){
            throw new IllegalArgumentException("Info Session was null!");
        }else if(infoSession.getWaterlooApiDAO() == null){
            throw new IllegalArgumentException("Info Session does not have data from Waterloo API!");
        }else{
            new GetPermalinkTask(infoSession, callback).execute();
        }
    }

    private static class GetPermalinkTask extends AsyncTask<Void, Void, Void>{

        private InfoSession mInfoSession;
        private Callback mCallback;

        public GetPermalinkTask(InfoSession infoSession, Callback callback){
            mInfoSession = infoSession;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                String result = getWebPage(PERMALINK_FILE);
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.getJSONArray("data");
                for(int i = arr.length() - 1; i >= 0; i --) {
                    JSONObject infoSessionDatum = arr.getJSONObject(i);
                    if (infoSessionDatum.getInt("id") == mInfoSession.getWaterlooApiDAO().getId()) {
                        mCallback.onSuccess(mInfoSession, infoSessionDatum.getString("permalink"));
                        return null;
                    } else if (infoSessionDatum.getString("employer").equals(mInfoSession.getWaterlooApiDAO().getEmployer())) {
                        mCallback.onSuccess(mInfoSession, infoSessionDatum.getString("permalink"));
                        return null;
                    } else if (infoSessionDatum.has("alt_names")) {
                        JSONArray arr_names = infoSessionDatum.getJSONArray("alt_names");
                        for (int j = arr_names.length() - 1; j >= 0; j--) {
                            if (arr_names.getString(j).equals(mInfoSession.getWaterlooApiDAO().getEmployer())) {
                                mCallback.onSuccess(mInfoSession, infoSessionDatum.getString("permalink"));
                                return null;
                            }
                        }
                    }
                }
                mCallback.onFailure(null);
            }catch(Exception e){
                mCallback.onFailure(e);
            }

            return null;
        }
    }
}
