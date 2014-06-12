package com.sixbynine.infosessions.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    private static final DateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("EEEE MMM d, yyyy");
    private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat("EEE MMM d, h:mma");

    private ListView mListView;
    private ArrayList<InfoSession> mInfoSessions;
    private Callback mCallback;

    //the Fragment should interact with its Activity by invoking Callback methods
    //feel free to add methods as necessary, I figured this was one that we're going to need for sure
    public interface Callback {
        public void onInfoSessionClicked(InfoSession infoSession);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("InfoSessionsListFragment", "onAttach");
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException(activity.getClass().toString() + " must implement interface Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("InfoSessionsListFragment", "onCreate");
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mInfoSessions = args.getParcelableArrayList("infoSessions");
            if (mInfoSessions == null) {
                throw new IllegalStateException("InfoSessions list was not provided!");
            }
        } else {
            mInfoSessions = savedInstanceState.getParcelableArrayList("infoSessions");
            if (mInfoSessions == null) {
                throw new IllegalStateException("InfoSessions list was not provided from onSavedInstance!");
            }
        }
        Collections.sort(mInfoSessions);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("InfoSessionsListFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_info_session_list, null);
        ListAdapter adapter = new InfoSessionListAdapter(mInfoSessions);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO: the user clicked an info session
    }

    /**
     * @author curtiskroetsch
     */
    public class InfoSessionListAdapter extends ArrayAdapter<InfoSession> {

        public InfoSessionListAdapter(List<InfoSession> sessions) {
            super(getActivity(), R.layout.info_session, R.id.companyName, sessions);
        }

        private int getDayOfYear(int row) {
            return getItem(row).waterlooApiDAO.getStartTime().get(Calendar.DAY_OF_YEAR);
        }

        /**
         * Test if info session i, is the last on its day
         * @param i The index of the info session
         * @return True if the session is the last on its day
         */
        private boolean isLastOfDay(int i) {
            return (i == getCount()-1) || isFirstOfDay(i+1);
        }

        /**
         * Test if info session i, is the first on its day
         * @param i The index of the info session
         * @return True if the sesssion is the first on its day
         */
        private boolean isFirstOfDay(int i) {
            return (i == 0) || (getDayOfYear(i) > getDayOfYear(i-1));
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = InfoSessionListFragment.this.getLayoutInflater(null);
            ViewHolder viewHolder;
            InfoSession infoSession = getItem(i);

            if (view == null) {
                view = inflater.inflate(R.layout.info_session, viewGroup, false);
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

            Date date = infoSession.waterlooApiDAO.getStartTime().getTime();
            Bitmap logo = (infoSession.companyInfo == null) ? null :
                    infoSession.companyInfo.getPrimaryImageBitmap();

            if (isFirstOfDay(i)) {
                viewHolder.dateHeader.setVisibility(View.VISIBLE);
                viewHolder.dateHeader.setText(HEADER_DATE_FORMAT.format(date));
            } else {
                viewHolder.dateHeader.setVisibility(View.GONE);
            }
            viewHolder.companyName.setText(infoSession.waterlooApiDAO.getEmployer());
            viewHolder.startTime.setText(TIME_DATE_FORMAT.format(date));
            viewHolder.location.setText(infoSession.waterlooApiDAO.getLocation());
            viewHolder.companyLogo.setImageBitmap(logo);
            viewHolder.cardLayout.setLastCategory(isLastOfDay(i));

            return view;
        }
    }

    static class ViewHolder {
        TextView dateHeader;
        TextView companyName;
        TextView startTime;
        TextView location;
        ImageView companyLogo;
        InfoSessionCardLayout cardLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("infoSessions", mInfoSessions);
    }
}
