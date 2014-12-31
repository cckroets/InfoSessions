package com.sixbynine.infosessions.net.serialization;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author curtiskroetsch
 */
public class InfoSessionDeserializer implements JsonDeserializer<WaterlooInfoSession> {

    private static final String TAG = InfoSessionDeserializer.class.getName();

    final DateFormat mDateFormat = new SimpleDateFormat("MMM d, yyyy");
    final DateFormat mTimeFormat = new SimpleDateFormat("h:m a");

    @Override
    public WaterlooInfoSession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        final JsonObject jsonObject = (JsonObject) json;

        final String id = jsonObject.get("id").getAsString();
        final String employer = jsonObject.get("employer").getAsString();
        final String rawDate = jsonObject.get("date").getAsString();
        final String rawStartTime = jsonObject.get("start_time").getAsString();
        final String rawEndTime = jsonObject.get("end_time").getAsString();
        final String location = jsonObject.get("location").getAsString();
        final String website = jsonObject.get("website").getAsString();
        final String audience = jsonObject.get("audience").getAsString();
        final String programs = jsonObject.get("programs").getAsString();
        final String description = jsonObject.get("description").getAsString();

        final Date date = parseDate(mDateFormat, rawDate);
        final Calendar startTime = parseFullDate(rawStartTime, date);
        final Calendar endTime = parseFullDate(rawEndTime, date);

        final boolean forCoops = audience.contains("Co-op");
        final boolean forGrads = audience.contains("Graduating Students");

        return new WaterlooInfoSession(id, employer, startTime, endTime, location, website,
                forCoops, forGrads, programs, description);
    }

    private Date parseDate(DateFormat dateFormat, String rawTime) {
        try {
            return dateFormat.parse(rawTime);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private Calendar parseFullDate(String rawTime, Date date) {

        final Calendar calendar = new GregorianCalendar();
        final Date time = parseDate(mTimeFormat, rawTime);

        calendar.setTimeInMillis(time.getTime() + date.getTime());
        return calendar;
    }
}