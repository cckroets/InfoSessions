package com.sixbynine.infosessions.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.CalendarContract;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by stevenkideckel on 15-01-02.
 */
public class InfoSessionUtil {

    public static void launchCalendarIntent(Context context, WaterlooInfoSession infoSession){
        Resources res = context.getResources();

        Calendar startDate = infoSession.getStartTime();
        Calendar endDate = infoSession.getEndTime();
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setType("vnd.android.cursor.item/event")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false)
                .putExtra(CalendarContract.Events.TITLE, res.getString(R.string.event_header, infoSession.getCompanyName()))
                .putExtra(CalendarContract.Events.DESCRIPTION, infoSession.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, infoSession.getLocation())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        context.startActivity(intent);
    }

}
