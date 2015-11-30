package com.sixbynine.infosessions.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.alarm.AlarmManager;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.event.data.InfoSessionPreferencesModifiedEvent;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by stevenkideckel on 15-01-02.
 */
@Singleton
public class InfoSessionUtil {

    @Inject
    InfoSessionPreferenceManager mPreferenceManager;

    @Inject
    AlarmManager mAlarmManager;

    public void shareInfoSession(Context context, WaterlooInfoSession infoSession) {
        DateFormat timeDateFormat = new SimpleDateFormat("EEE MMM d, h:mma");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_message,
                infoSession.getCompanyName(),
                infoSession.getLocation(),
                timeDateFormat.format(infoSession.getStartTime().getTime()),
                infoSession.getId()));
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void launchCalendarIntent(Context context, WaterlooInfoSession infoSession) {
        Resources res = context.getResources();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setType("vnd.android.cursor.item/event")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, infoSession.getStartTime().getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, infoSession.getEndTime().getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                .putExtra(CalendarContract.Events.TITLE, res.getString(R.string.event_header, infoSession.getCompanyName()))
                .putExtra(CalendarContract.Events.DESCRIPTION, infoSession.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, infoSession.getLocation())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        context.startActivity(intent);
    }

    public void rsvp(Context context, WaterlooInfoSession infoSession) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://info.uwaterloo.ca/infocecs/students/rsvp/index.php?id="
                + infoSession.getId() + "&mode=on"));
        context.startActivity(intent);
    }

    public void cancelRsvp(Context context, WaterlooInfoSession infoSession) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://info.uwaterloo.ca/infocecs/students/rsvp/index.php?id=" +
                infoSession.getId() + "&mode=off"));
        context.startActivity(intent);
    }

    private int mAlarmChoice;

    public void doAlarmLogic(Activity context, final WaterlooInfoSession infoSession) {
        Resources res = context.getResources();
        WaterlooInfoSessionPreferences prefs = mPreferenceManager.getPreferences(infoSession);
        if (prefs.hasAlarm()) {
            int minutes = prefs.getAlarm();
            String message;
            if (minutes >= 60) {
                message = res.getQuantityString(R.plurals.remove_alarm_message_hours, minutes / 60, minutes / 60);
            } else {
                message = res.getQuantityString(R.plurals.remove_alarm_message_minutes, minutes, minutes);
            }

            new AlertDialog.Builder(context)
                    .setTitle(R.string.remove_alarm)
                    .setMessage(message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlarmManager.cancelAlarm(infoSession);
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(),
                                    "Reminder removed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        } else {
            mAlarmChoice = 0;
            new AlertDialog.Builder(context)
                    .setTitle(R.string.alarm_choices_title)
                            //.setMessage(R.string.alarm_choices_prompt)
                    .setSingleChoiceItems(R.array.alarm_choices, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlarmChoice = which;
                        }
                    })
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int minutes = MyApplication.getInstance().getResources()
                                    .getIntArray(R.array.alarm_choices_minutes_values)[mAlarmChoice];
                            mAlarmManager.setAlarm(infoSession, minutes);
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(),
                                    "Reminder added", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
                    .create()
                    .show();
        }
    }

}
