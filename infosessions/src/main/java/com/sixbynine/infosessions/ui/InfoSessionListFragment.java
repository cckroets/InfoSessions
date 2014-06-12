package com.sixbynine.infosessions.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import com.sixbynine.infosessions.adapter.InfoSessionListAdapter;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.company.Company;

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
    private Callback mCallback;
    private InfoSessionListAdapter mAdapter;
    private Handler mHandler;

    public interface Callback {
        public void onInfoSessionClicked(InfoSession infoSession);

        public ArrayList<InfoSession> getInfoSessions();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException(activity.getClass().toString() + " must implement interface Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_session_list, null);
        mHandler = new Handler();
        Collections.sort(mCallback.getInfoSessions());
        mAdapter = new InfoSessionListAdapter(getActivity(), mCallback.getInfoSessions());
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        for (InfoSession infoSession : mCallback.getInfoSessions()) {
            if (infoSession.getCompanyInfo() == null) {
                infoSession.addOnDataLoadedListener(mOnDataLoadedListener);
            } else if (infoSession.getCompanyInfo().getPrimaryImageBitmap() == null) {
                infoSession.getCompanyInfo().addOnImageStatusChangedListener(onImageStatusChangedListener);
            }
        }

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (mCallback != null && mAdapter != null)
            mCallback.onInfoSessionClicked(mAdapter.getItem(position));
    }

    private InfoSession.OnDataLoadedListener mOnDataLoadedListener = new InfoSession.OnDataLoadedListener() {
        @Override
        public void onDataLoaded(InfoSession infoSession) {
            if (infoSession.getCompanyInfo() != null) {
                if (infoSession.getCompanyInfo().getPrimaryImageBitmap() != null) {
                    refreshListView();
                } else {
                    infoSession.getCompanyInfo().addOnImageStatusChangedListener(onImageStatusChangedListener);
                }
            }
        }
    };

    private Company.OnImageStatusChangedListener onImageStatusChangedListener = new Company.OnImageStatusChangedListener() {
        @Override
        public void onImageStatusChanged(int newStatus) {
            if (newStatus == Company.IMAGE_LOADED) {
                refreshListView();
            }
        }
    };

    private void refreshListView() {
        if (!this.isDetached()) {
            mHandler.post(new Runnable() {
                public void run() {
                    if (mAdapter != null) mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
