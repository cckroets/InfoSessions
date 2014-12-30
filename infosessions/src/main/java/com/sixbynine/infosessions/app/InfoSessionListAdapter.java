package com.sixbynine.infosessions.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.InfoSessionSaver;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.ui.SwipeDismissListViewTouchListener;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.ui.ViewUtil;
import com.sixbynine.infosessions.util.Logger;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import roboguice.RoboGuice;

/**
 * @author curtiskroetsch
 */
public class InfoSessionListAdapter extends ArrayAdapter<WaterlooInfoSession> {

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @Inject
    InfoSessionManager mInfoSessionManager;

    public interface InfoSessionActionListener{
        public void onFavoriteClicked(WaterlooInfoSession infoSession);
        public void onShareClicked(WaterlooInfoSession infoSession);
        public void onTimerClicked(WaterlooInfoSession infoSession);
        public void onDismiss(WaterlooInfoSession infoSession);
        public void onInfoSessionClicked(WaterlooInfoSession infoSession);
    }

    private static final DateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("EEEE MMM d, yyyy");
    private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat("EEE MMM d, h:mma");

    private InfoSessionActionListener mListener;
    private ListView mListView;

    public InfoSessionListAdapter(Context context, List<WaterlooInfoSession> sessions, ListView listView) {
        super(context, R.layout.info_session, R.id.companyName, sessions);
        mListView = listView;
        RoboGuice.getInjector(getContext()).injectMembersWithoutViews(this);
        /*SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mListView,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    if(mListener != null){
                                        mListener.onDismiss(getItem(position));
                                    }
                                }
                            }
                        });
        mListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mListView.setOnScrollListener(touchListener.makeScrollListener());*/
    }

    private int getDayOfYear(int row) {
        return getItem(row).getStartTime().get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Test if info session i, is the last on its day
     *
     * @param i The index of the info session
     * @return True if the session is the last on its day
     */
    private boolean isLastOfDay(int i) {
        return (i == getCount() - 1) || isFirstOfDay(i + 1);
    }

    /**
     * Test if info session i, is the first on its day
     *
     * @param i The index of the info session
     * @return True if the sesssion is the first on its day
     */
    private boolean isFirstOfDay(int i) {
        return (i == 0) || (getDayOfYear(i) > getDayOfYear(i - 1));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final WaterlooInfoSession infoSession = getItem(i);
        final WaterlooInfoSessionPreferences preferences =
                mInfoSessionPreferenceManager.getPreferences(infoSession);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.info_session_row, viewGroup, false);
            viewHolder = new ViewHolder();
            //viewHolder.dateHeader = (TextView) view.findViewById(R.id.dateHeader);
            viewHolder.companyName = (TextView) view.findViewById(R.id.company_name);
            viewHolder.startTime = (TextView) view.findViewById(R.id.start_time);
            viewHolder.location = (TextView) view.findViewById(R.id.location);
            viewHolder.companyLogo = (ImageView) view.findViewById(R.id.company_logo);
            viewHolder.shareButton = (ImageButton) view.findViewById(R.id.share_button);
            viewHolder.timerButton = (ImageButton) view.findViewById(R.id.timer_button);
            viewHolder.favoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
            viewHolder.clickableRegion = view.findViewById(R.id.clickable_region_container);
            //viewHolder.cardLayout = (InfoSessionCardLayout) view.findViewById(R.id.card);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Date date = infoSession.getStartTime().getTime();
        viewHolder.companyLogo.setImageBitmap(null);
        Picasso.with(getContext()).cancelRequest(viewHolder.companyLogo);
        mInfoSessionManager.getCompanyFromSession(infoSession.getId(), new ResponseHandler<Company>() {
            @Override
            public void onSuccess(Company object) {
                if(object != null) {
                    Picasso.with(getContext())
                            .load("https://res.cloudinary.com/crunchbase-production/" + object.getPrimaryImageUrl())
                            .transform(ViewUtil.createLogoTransformation())
                            .into(viewHolder.companyLogo);
                }else{
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
        if(preferences.isFavorited()){
            viewHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite);
        }else{
            viewHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_outline);
        }

        //viewHolder.cardLayout.setLastCategory(isLastOfDay(i));

        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) mListener.onFavoriteClicked(infoSession);
            }
        });
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) mListener.onShareClicked(infoSession);
            }
        });
        viewHolder.timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onTimerClicked(infoSession);
            }
        });
        viewHolder.clickableRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) mListener.onInfoSessionClicked(infoSession);
            }
        });

        return view;
    }

    public void setActionListener(InfoSessionActionListener listener){
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("InfoSessions", "notifyDataSetChanged called");
    }

    static class ViewHolder {
        //TextView dateHeader;
        TextView companyName;
        TextView startTime;
        TextView location;
        ImageView companyLogo;
        //InfoSessionCardLayout cardLayout;
        ImageButton timerButton;
        ImageButton shareButton;
        ImageButton favoriteButton;
        View clickableRegion;
    }
}


