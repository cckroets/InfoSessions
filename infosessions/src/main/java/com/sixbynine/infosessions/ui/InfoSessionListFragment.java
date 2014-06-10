package com.sixbynine.infosessions.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
* @author curtiskroetsch
*/
public class InfoSessionListFragment extends ListFragment {


    private List<InfoSession> sessions;

    // TODO: Use Bundle instead of raw type
    public InfoSessionListFragment(List<InfoSession> sessions) {
        this.sessions = sessions;
        Collections.sort(sessions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListAdapter adapter = new InfoSessionListAdapter(sessions);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setDivider(null);
        listView.setDividerHeight(10);
        listView.setBackgroundResource(R.color.info_sessions_fragment_bg);
        listView.setBackgroundColor(Color.rgb(230,230,230));
        listView.setPadding(15,0,15,0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    /**
     * @author curtiskroetsch
     */
    public class InfoSessionListAdapter extends BaseAdapter {

        List<Row> rows;

        public InfoSessionListAdapter(List<InfoSession> sessions) {

            this.rows = new ArrayList<Row>(sessions.size());
            int lastDay = -1;

            for (InfoSession session : sessions) {
                Calendar calendar = session.waterlooApiDAO.getStartTime();
                int day = calendar.get(Calendar.DAY_OF_YEAR);
                if (lastDay == -1 || lastDay < day) {
                    rows.add(new DateHeader(calendar.getTime()));
                    lastDay = day;
                }
                rows.add(new InfoSessionRow(session));
            }
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int i) {
            return rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = InfoSessionListFragment.this.getLayoutInflater(null);
            view = rows.get(i).getView(inflater,view,viewGroup);
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return rows.get(position).getType();
        }

        @Override
        public int getViewTypeCount() {
            return Row.NUM_TYPES;
        }

    }

    /**
     * A Row represents a single list item in the InfoSessionListFragment.
     * It is one of:
     * * A DateHeader - Separator to segregate info sessions that happen on different days
     * * An InfoSessionRow - An InfoSession card with some information
     *
     * @author curtiskroetsch
     */
    public interface Row {

        public int getType();
        public View getView(LayoutInflater inflater, View view, ViewGroup viewGroup);

        public static int TYPE_INFO_SESSION = 0;
        public static int TYPE_HEADER = 1;
        public static int NUM_TYPES = 2;
    }

    /**
     * DateHeader - A single text field that displays the day of the year
     * for the following info sessions
     */
    public static class DateHeader implements Row {

        private static DateFormat DAY_FORMAT = new SimpleDateFormat("EEEE MMM d, yyyy");

        private Date date;

        public DateHeader(Date date) {
            this.date = date;
        }

        public int getType() {
            return TYPE_HEADER;
        }

        @Override
        public View getView(LayoutInflater inflater, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.info_session_date, viewGroup, false);
            }
            UIUtil.setTextForView(R.id.dateHeader, view, DAY_FORMAT.format(date));
            return view;
        }
    }

    /**
     * InfoSessionRow - A snippet of information about an InfoSession
     */
    public static class InfoSessionRow implements Row {

        private static final DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mma");

        private InfoSession infoSession;

        public InfoSessionRow(InfoSession infoSession) {
            this.infoSession = infoSession;
        }

        @Override
        public int getType() {
            return Row.TYPE_INFO_SESSION;
        }

        @Override
        public View getView(LayoutInflater inflater, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.info_session, viewGroup, false);
            }

            UIUtil.setTextForView(R.id.companyName, view, infoSession.waterlooApiDAO.getEmployer());
            UIUtil.setTextForView(R.id.startTime, view, dateFormat.format(infoSession.waterlooApiDAO.getStartTime().getTime()));
            UIUtil.setTextForView(R.id.location, view, infoSession.waterlooApiDAO.getLocation());

            ImageView logo = (ImageView) view.findViewById(R.id.companyLogo);
            if (infoSession.companyInfo != null && infoSession.companyInfo.getPrimaryImageBitmap() != null) {
                logo.setImageBitmap(infoSession.companyInfo.getPrimaryImageBitmap());
            } else {
                logo.setImageBitmap(null);
            }


            return view;
        }
    }


}
