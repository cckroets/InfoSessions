package com.sixbynine.infosessions.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.flurry.sdk.de;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.CompanyInfoActivity;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.event.data.InfoSessionPreferencesModifiedEvent;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.util.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import roboguice.RoboGuice;

/**
 * Created by steviekideckel on 2015-01-06.
 */
@Singleton
public class AlarmManager{

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    private static final String KEY_INFO_SESSION = "info-session-key";
    private static final int NOTIFICATION_ID = 1337;


    public static class AlarmReceiver extends BroadcastReceiver{

        @Inject
        PreferenceManager mPreferenceManager;

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("OnReceiveIntentCalled");
            RoboGuice.getInjector(context).injectMembersWithoutViews(this);

            WaterlooInfoSession infoSession = intent.getParcelableExtra(KEY_INFO_SESSION);

            DateFormat timeFormat = new SimpleDateFormat("h:mm");
            Resources res = context.getResources();
            String title = res.getString(R.string.event_header, infoSession.getCompanyName());
            String message = res.getString(R.string.time_in_place,
                    timeFormat.format(infoSession.getStartTime().getTime()),
                    infoSession.getLocation());

            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                    .setContentText(message);

            Intent resultIntent = new Intent(context, CompanyInfoActivity.class);
            resultIntent.putExtra(CompanyInfoActivity.INFO_SESSION_KEY, infoSession);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(CompanyInfoActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);

            NotificationPreference pref = NotificationPreference.getNotificationPreference(mPreferenceManager);
            if(pref.hasVibrate()){
                builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            }
            if(pref.hasLights()){
                builder.setLights(Color.GREEN, 3000, 3000);
            }
            if(pref.hasSound()){
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public void setAlarm(WaterlooInfoSession infoSession, int minutesPreceding){
        Context context = MyApplication.getInstance().getApplicationContext();
        long millisOfEvent = infoSession.getStartTime().getTimeInMillis();
        long millisOfAlarm = millisOfEvent - minutesPreceding * 60 * 1000;

        android.app.AlarmManager manager =
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.set(android.app.AlarmManager.RTC_WAKEUP, millisOfAlarm, getInfoSessionPendingIntent(infoSession));
        WaterlooInfoSessionPreferences prefs = mInfoSessionPreferenceManager.editPreferences(infoSession)
                .addAlarm(minutesPreceding)
                .commit();
        MainBus.get().post(new InfoSessionPreferencesModifiedEvent(infoSession, prefs));
    }

    public void setTestAlarm(WaterlooInfoSession infoSession){
        Context context = MyApplication.getInstance().getApplicationContext();

        android.app.AlarmManager manager =
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.set(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, getInfoSessionPendingIntent(infoSession));
        /*mInfoSessionPreferenceManager.editPreferences(infoSession)
                .addAlarm(minutesPreceding)
                .commit();*/
    }

    public void cancelAlarm(WaterlooInfoSession infoSession){
        Context context = MyApplication.getInstance().getApplicationContext();
        android.app.AlarmManager manager =
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(getInfoSessionPendingIntent(infoSession));
        WaterlooInfoSessionPreferences prefs = mInfoSessionPreferenceManager.editPreferences(infoSession)
                .removeAlarm()
                .commit();
        MainBus.get().post(new InfoSessionPreferencesModifiedEvent(infoSession, prefs));
    }

    public void cancelActiveNotifications(){
        Context context = MyApplication.getInstance().getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    private static PendingIntent getInfoSessionPendingIntent(WaterlooInfoSession infoSession){
        Context context = MyApplication.getInstance().getApplicationContext();
        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra(KEY_INFO_SESSION, infoSession);
        return PendingIntent.getBroadcast(context, Integer.parseInt(infoSession.getId()),
                i, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
    }



}
