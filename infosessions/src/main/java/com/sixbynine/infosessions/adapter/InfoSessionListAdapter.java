package com.sixbynine.infosessions.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.ui.InfoSessionCardLayout;
import com.sixbynine.infosessions.ui.InfoSessionListFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author curtiskroetsch
 */
public class InfoSessionListAdapter extends ArrayAdapter<InfoSession> {

    private static final DateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("EEEE MMM d, yyyy");
    private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat("EEE MMM d, h:mma");

    static class ViewHolder {
        TextView dateHeader;
        TextView companyName;
        TextView startTime;
        TextView location;
        ImageView companyLogo;
        InfoSessionCardLayout cardLayout;
    }

    public InfoSessionListAdapter(Context context, List<InfoSession> sessions) {
        super(context, R.layout.info_session, R.id.companyName, sessions);
    }

    private int getDayOfYear(int row) {
        return getItem(row).getWaterlooApiDAO().getStartTime().get(Calendar.DAY_OF_YEAR);
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
        ViewHolder viewHolder;
        InfoSession infoSession = getItem(i);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.info_session, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.dateHeader = (TextView) view.findViewById(R.id.dateHeader);
            viewHolder.companyName = (TextView) view.findViewById(R.id.companyName);
            viewHolder.startTime = (TextView) view.findViewById(R.id.startTime);
            viewHolder.location = (TextView) view.findViewById(R.id.location);
            viewHolder.companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
            viewHolder.cardLayout = (InfoSessionCardLayout) view.findViewById(R.id.card);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Date date = infoSession.getWaterlooApiDAO().getStartTime().getTime();
        Bitmap logo = (infoSession.getCompanyInfo() == null) ? null :
                infoSession.getCompanyInfo().getPrimaryImageBitmap();
        if (isFirstOfDay(i)) {
            viewHolder.dateHeader.setVisibility(View.VISIBLE);
            viewHolder.dateHeader.setText(HEADER_DATE_FORMAT.format(date));
        } else {
            viewHolder.dateHeader.setVisibility(View.GONE);
        }
        viewHolder.companyName.setText(infoSession.getWaterlooApiDAO().getEmployer());
        viewHolder.startTime.setText(TIME_DATE_FORMAT.format(date));
        viewHolder.location.setText(infoSession.getWaterlooApiDAO().getLocation());
        viewHolder.companyLogo.setImageBitmap(logo);
        viewHolder.cardLayout.setLastCategory(isLastOfDay(i));

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("InfoSessions", "notifyDataSetChanged called");
    }
}


