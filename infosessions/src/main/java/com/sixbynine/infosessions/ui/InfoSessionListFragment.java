package com.sixbynine.infosessions.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView;
import android.widget.AdapterView;
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

@SuppressLint("ValidFragment")
/**
 * @author curtiskroetsch
 */
public class InfoSessionListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private ListView mListView;
    private List<InfoSession> sessions;

    // TODO: Use Bundle instead of raw type

    public InfoSessionListFragment(List<InfoSession> sessions) {
        this.sessions = sessions;
        Collections.sort(sessions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_session_list, null);
        ListAdapter adapter = new InfoSessionListAdapter(sessions);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                if (lastDay < day) {
                    if (lastDay != -1) {
                        setLastRow();
                    }
                    rows.add(new DateHeader(calendar.getTime()));
                    lastDay = day;
                }
                rows.add(new InfoSessionRow(session));
            }
            setLastRow();
        }

        /**
         * Set the isLast field to true for the last added info session
         */
        private void setLastRow() {
            InfoSessionRow sessionRow = (InfoSessionRow) rows.get(rows.size()-1);
            sessionRow.setIsLast(true);
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

        private InfoSession mInfoSession;
        private boolean mIsLast = false;

        public InfoSessionRow(InfoSession infoSession) {
            this.mInfoSession = infoSession;
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

            InfoSessionCardLayout layout = (InfoSessionCardLayout) view;
            layout.setLastCategory(mIsLast);

            UIUtil.setTextForView(R.id.companyName, view, mInfoSession.waterlooApiDAO.getEmployer());
            UIUtil.setTextForView(R.id.startTime, view, dateFormat.format(mInfoSession.waterlooApiDAO.getStartTime().getTime()));
            UIUtil.setTextForView(R.id.location, view, mInfoSession.waterlooApiDAO.getLocation());

            ImageView logo = (ImageView) view.findViewById(R.id.companyLogo);
            if (mInfoSession.companyInfo != null && mInfoSession.companyInfo.getPrimaryImageBitmap() != null) {
                logo.setImageBitmap(mInfoSession.companyInfo.getPrimaryImageBitmap());
            } else {
                logo.setImageBitmap(null);
            }

            return view;
        }

        public void setIsLast(boolean val) {
            this.mIsLast = val;
        }
    }


}
