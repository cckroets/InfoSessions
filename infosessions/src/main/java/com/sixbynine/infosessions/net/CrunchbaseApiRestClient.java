package com.sixbynine.infosessions.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * General REST client to make GET calls to the Crunchbase API
 */
public class CrunchbaseApiRestClient {


    private static final String API_ROOT = "http://api.crunchbase.com/v/2";

    public interface Callback {
        public void onSuccess(JSONObject obj);

        public void onFailure(Throwable e);
    }

    private static String getWebPage(String url) throws IllegalStateException, IOException {
        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse execute = client.execute(httpGet);
        InputStream content = execute.getEntity().getContent();

        BufferedReader buffer = new BufferedReader(
                new InputStreamReader(content));
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s = buffer.readLine()) != null) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Makes a get call to the Crunchbase API.  You do not need to supply the API key as this will be added to list of parameters regardless.
     *
     * @param page     the API resource you wish to access, e.g. /organizations
     * @param params   the list of any parameters to add on to the get request, can be null
     * @param callback a listener that the get response can call back to, cannot be null
     */
    public static void get(String page, Map<String, String> params, Callback callback) {
        StringBuilder requestURL = new StringBuilder(API_ROOT);
        requestURL.append(page)
                .append("?user_key=")
                .append(Keys.API_KEY_CRUNCHBASE);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> paramsIter = params.entrySet().iterator();
            while (paramsIter.hasNext()) {
                requestURL.append("&");
                Map.Entry<String, String> entry = paramsIter.next();
                requestURL.append(entry.getKey());
                requestURL.append("=");
                if (entry.getValue().contains(" ")) {
                    requestURL.append("\"").append(entry.getValue()).append("\"");
                } else {
                    requestURL.append(entry.getValue());
                }
            }
        }
        try {
            String pageRaw = getWebPage(requestURL.toString());
            JSONObject obj = new JSONObject(pageRaw);
            callback.onSuccess(obj);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            callback.onFailure(e);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(e);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFailure(e);
        }

    }
}
