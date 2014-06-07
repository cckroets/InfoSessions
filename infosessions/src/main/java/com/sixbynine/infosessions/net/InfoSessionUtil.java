package com.sixbynine.infosessions.net;

import android.os.AsyncTask;

import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Class to make calls specific to Info Sessions.  The calling class should invoke the
 * getInfoSessions method and pass in a callback.
 */
public class InfoSessionUtil {

    private static AsyncInfoSessionFetcher sAsyncInfoSessionFetcher;

    public interface InfoSessionsCallback {
        public void onSuccess(List<InfoSessionWaterlooApiDAO> infoSessions);

        public void onFailure(Throwable e);
    }

    public static void getInfoSessions(InfoSessionsCallback callback) {
        if (sAsyncInfoSessionFetcher == null || sAsyncInfoSessionFetcher.getStatus().equals(AsyncTask.Status.FINISHED)) {
            sAsyncInfoSessionFetcher = new AsyncInfoSessionFetcher();
            sAsyncInfoSessionFetcher.execute(callback);
        }
    }

    private static class CallProcessor implements WaterlooApiRestClient.Callback {
        private InfoSessionsCallback callback;

        public CallProcessor(InfoSessionsCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(JSONObject obj) {
            try {
                JSONArray data = obj.getJSONArray("data");
                int len = data.length();
                List<InfoSessionWaterlooApiDAO> result = new ArrayList<InfoSessionWaterlooApiDAO>(len);
                for (int i = 0; i < len; i++) {
                    JSONObject raw = data.getJSONObject(i);
                    InfoSessionWaterlooApiDAO infoSession = new InfoSessionWaterlooApiDAO(raw.getInt("id"));
                    infoSession.setEmployer(raw.getString("employer"));

                    Calendar date = parseDate(raw.getString("date"));

                    long startTime = parseTime(raw.getString("start_time"));
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTimeInMillis(date.getTimeInMillis());
                    startCal.set(Calendar.HOUR_OF_DAY, (int) (startTime / 60));
                    startCal.set(Calendar.MINUTE, (int) (startTime % 60));
                    //startCal.setTimeInMillis(date.getTimeInMillis() + startTime);
                    infoSession.setStartTime(startCal);

                    long endTime = parseTime(raw.getString("end_time"));
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTimeInMillis(date.getTimeInMillis());
                    endCal.set(Calendar.HOUR_OF_DAY, (int) (endTime / 60));
                    endCal.set(Calendar.MINUTE, (int) (endTime % 60));
                    //endCal.setTimeInMillis(date.getTimeInMillis() + endTime);
                    infoSession.setEndTime(endCal);

                    infoSession.setLocation(raw.getString("location"));
                    infoSession.setWebsite(raw.getString("website"));

                    String audience = raw.getString("audience");
                    infoSession.setForCoopStudents(audience.contains("Co-op"));
                    infoSession.setForGraduatingStudents(audience.contains("Graduating"));
                    String[] programs = raw.getString("programs").split(",");
                    ArrayList<String> programsArrayList = new ArrayList<String>(programs.length);
                    for (int j = 0; j < programs.length; j++) {
                        programsArrayList.add(programs[j]);
                    }
                    infoSession.setPrograms(programsArrayList);
                    infoSession.setDescription(raw.getString("description"));
                    result.add(infoSession);

                }
                callback.onSuccess(result);
            } catch (JSONException e) {
                callback.onFailure(e);
            }

        }

        @Override
        public void onFailure(Throwable e) {
            callback.onFailure(e);

        }
    }

    private static Calendar parseDate(String date) {
        String[] parts = date.split(",");
        String[] monthDay = parts[0].split(" ");
        int year = Integer.parseInt(parts[1].trim());
        int day = Integer.parseInt(monthDay[1]);
        String monthString = monthDay[0];
        int month = 0;
        if (monthString.equals("January")) {
            month = Calendar.JANUARY;
        } else if (monthString.equalsIgnoreCase("February")) {
            month = Calendar.FEBRUARY;
        } else if (monthString.equalsIgnoreCase("March")) {
            month = Calendar.MARCH;
        } else if (monthString.equalsIgnoreCase("April")) {
            month = Calendar.APRIL;
        } else if (monthString.equalsIgnoreCase("May")) {
            month = Calendar.MAY;
        } else if (monthString.equalsIgnoreCase("June")) {
            month = Calendar.JUNE;
        } else if (monthString.equalsIgnoreCase("July")) {
            month = Calendar.JULY;
        } else if (monthString.equalsIgnoreCase("August")) {
            month = Calendar.AUGUST;
        } else if (monthString.equalsIgnoreCase("September")) {
            month = Calendar.SEPTEMBER;
        } else if (monthString.equalsIgnoreCase("October")) {
            month = Calendar.OCTOBER;
        } else if (monthString.equalsIgnoreCase("November")) {
            month = Calendar.NOVEMBER;
        } else if (monthString.equalsIgnoreCase("December")) {
            month = Calendar.DECEMBER;
        } else {
            throw new IllegalStateException("Month not formatted properly: " + monthString);
        }

        return new GregorianCalendar(year, month, day);


    }

    private static long parseTime(String time) {
        String[] parts = time.split(" ");
        String[] hm = parts[0].split(":");
        int hours = Integer.parseInt(hm[0]);
        int minutes = Integer.parseInt(hm[1]);
        return hours * 60 + minutes + (parts[1].equalsIgnoreCase("PM") ? 12 * 60 : 0);
    }

    private static class AsyncInfoSessionFetcher extends AsyncTask<InfoSessionsCallback, Void, Void> {


        @Override
        protected Void doInBackground(InfoSessionsCallback... callbacks) {
            if (callbacks.length == 0) {
                throw new IllegalArgumentException("Must provide a callback");
            } else if (callbacks[0] == null) {
                throw new IllegalArgumentException("Provided callback is null");
            }
            InfoSessionsCallback callback = callbacks[0];
            WaterlooApiRestClient.get("/resources/infosessions", null, new CallProcessor(callback));

            return null;
        }
    }

}
