package com.sixbynine.infosessions.home;

import com.google.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sixbynine.infosessions.BuildConfig;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.alarm.AlarmManager;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.ui.CheatSheet;
import com.sixbynine.infosessions.util.CompatUtil;
import com.sixbynine.infosessions.util.Logger;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import roboguice.RoboGuice;

/**
 * @author curtiskroetsch
 */
public final class InfoSessionListAdapter extends ArrayAdapter<WaterlooInfoSession> {

    private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat("EEE MMM d, h:mma");

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionManager mInfoSessionManager;

    @Inject
    AlarmManager mAlarmManager;

    Activity mContext;

    private InfoSessionActionListener mListener;

    public InfoSessionListAdapter(Activity context, List<WaterlooInfoSession> sessions) {
        super(context, R.layout.info_session, R.id.companyName, sessions);
        mContext = context;
        RoboGuice.getInjector(context).injectMembersWithoutViews(this);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final WaterlooInfoSession infoSession = getItem(i);
        final WaterlooInfoSessionPreferences preferences =
                mInfoSessionPreferenceManager.getPreferences(infoSession);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.info_session_row, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            if (!CompatUtil.canHandleCalendarIntent(mContext)) {
                viewHolder.calendarButton.setVisibility(View.GONE); //calendar intent only works in 14 and higher
            }

            CheatSheet.setup(viewHolder.calendarButton, R.string.calendar_tooltip);
            CheatSheet.setup(viewHolder.shareButton, R.string.share_tooltip);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Date date = infoSession.getStartTime().getTime();
        viewHolder.companyLogo.setImageBitmap(null);
        Picasso.with(getContext()).cancelRequest(viewHolder.companyLogo);
        mInfoSessionManager.getCompanyFromInfoSession(infoSession, new ResponseHandler<Company>() {
            @Override
            public void onSuccess(Company object) {
                if (object != null) {
                    Picasso.with(getContext())
                            .load("https://res.cloudinary.com/crunchbase-production/" + object.getPrimaryImageUrl())
                            .into(viewHolder.companyLogo);
                } else {
                    Logger.e("Null object returned for %s", infoSession.getCompanyName());
                }
            }

            @Override
            public void onFailure(Exception error) {

            }
        });
        /*if (isFirstOfDay(i)) {
            viewHolder.dateHeader.setVisibility(View.VISIBLE);
            viewHolder.dateHeader.setText(HEADER_DATE_FORMAT.format(date));
        } else {
            viewHolder.dateHeader.setVisibility(View.GONE);
        }*/
        viewHolder.companyName.setText(infoSession.getCompanyName());
        viewHolder.startTime.setText(TIME_DATE_FORMAT.format(date));
        viewHolder.location.setText(infoSession.getLocation());
        if (preferences.isFavorited()) {
            viewHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite);
            CheatSheet.setup(viewHolder.favoriteButton, R.string.remove_favorite);
        } else {
            viewHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_outline);
            CheatSheet.setup(viewHolder.favoriteButton, R.string.add_favorite);
        }
        if (preferences.hasAlarm()) {
            viewHolder.alarmButton.setImageResource(R.drawable.ic_action_alarm_on);
            CheatSheet.setup(viewHolder.alarmButton, R.string.remove_reminder);
        } else {
            viewHolder.alarmButton.setImageResource(R.drawable.ic_action_alarm_add);
            CheatSheet.setup(viewHolder.alarmButton, R.string.add_reminder);
        }

        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireEvent(Event.FAVORITE, infoSession);
            }
        });
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireEvent(Event.SHARE, infoSession);
            }
        });
        viewHolder.alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireEvent(Event.ALARM, infoSession);
            }
        });
        viewHolder.calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireEvent(Event.CALENDAR, infoSession);
            }
        });
        viewHolder.clickableRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireEvent(Event.CLICK, infoSession);
            }
        });
        if (BuildConfig.DEBUG) {
            viewHolder.clickableRegion.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Generate test notification?")
                            .setMessage("The notification will be shown in 5 seconds")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAlarmManager.setTestAlarm(infoSession);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create()
                            .show();
                    return true;
                }
            });
        }

        return view;
    }

    public void setActionListener(InfoSessionActionListener listener) {
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("InfoSessions", "notifyDataSetChanged called");
    }

    private void fireEvent(Event event, WaterlooInfoSession infoSession) {
        if (mListener != null) {
            mListener.onInfoSessionEvent(event, infoSession);
        }
    }

    public enum Event {
        FAVORITE, SHARE, ALARM, CALENDAR, DISMISS, CLICK
    }

    public interface InfoSessionActionListener {
        void onInfoSessionEvent(Event event, WaterlooInfoSession infoSession);
    }

    static class ViewHolder {
        TextView companyName;
        TextView startTime;
        TextView location;
        ImageView companyLogo;
        ImageButton alarmButton;
        ImageButton calendarButton;
        ImageButton shareButton;
        ImageButton favoriteButton;
        View clickableRegion;

        ViewHolder(View view) {
            companyName = (TextView) view.findViewById(R.id.company_name);
            startTime = (TextView) view.findViewById(R.id.start_time);
            location = (TextView) view.findViewById(R.id.location);
            companyLogo = (ImageView) view.findViewById(R.id.company_logo);
            shareButton = (ImageButton) view.findViewById(R.id.share_button);
            alarmButton = (ImageButton) view.findViewById(R.id.alarm_button);
            favoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
            calendarButton = (ImageButton) view.findViewById(R.id.calendar_button);
            clickableRegion = view.findViewById(R.id.clickable_region_container);
        }
    }
}
