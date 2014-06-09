package com.sixbynine.infosessions.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
* @author curtiskroetsch
*/
public class InfoSessionListFragment extends ListFragment {

    private static final DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mma");

    private List<InfoSession> sessions;

    public InfoSessionListFragment(List<InfoSession> sessions) {
        this.sessions = sessions;
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
        listView.setBackgroundColor(Color.LTGRAY);
        listView.setPadding(15,15,15,15);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    /**
     * @author curtiskroetsch
     */
    public class InfoSessionListAdapter extends BaseAdapter {

        List<InfoSession> infoSessions;

        public InfoSessionListAdapter(List<InfoSession> sessions) {
            this.infoSessions = sessions;
        }

        @Override
        public int getCount() {
            return infoSessions.size();
        }

        @Override
        public Object getItem(int i) {
            return infoSessions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater inflater = InfoSessionListFragment.this.getLayoutInflater(null);
                view = inflater.inflate(R.layout.info_session, viewGroup, false);
            }

            InfoSession session = infoSessions.get(i);
            setTextForView(R.id.companyName, view, session.waterlooApiDAO.getEmployer());
            setTextForView(R.id.startTime, view, dateFormat.format(session.waterlooApiDAO.getStartTime().getTime()));
            setTextForView(R.id.location, view, session.waterlooApiDAO.getLocation());

            // TODO: Add code to fill in Company Logo if it exist
            // ImageView logo = (ImageView) view.findViewById(R.id.companyLogo);
            // logo.setImageBitmap();

            return view;
        }
    }

    /**
     * Set the text for an inner text view inside of view
     *
     * @param id The id of the inner text view
     * @param view The parent of the text view
     * @param text The new text for the text view
     */
    public static void setTextForView(int id, View view, String text) {
        ((TextView) view.findViewById(id)).setText(text);
    }
}
