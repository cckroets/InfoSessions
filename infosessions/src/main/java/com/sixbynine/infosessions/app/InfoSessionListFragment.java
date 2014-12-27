package com.sixbynine.infosessions.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

@SuppressLint("ValidFragment")
/**
 * @author curtiskroetsch
 */
public class InfoSessionListFragment extends RoboFragment implements AbsListView.OnItemClickListener {

    @Inject
    InfoSessionManager mInfoSessionManager;

    @InjectView(R.id.listView)
    ListView mListView;

    InfoSessionListAdapter mAdapter;

    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_session_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler();
        mAdapter = new InfoSessionListAdapter(getActivity(), new ArrayList<WaterlooInfoSession>());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                mAdapter.clear();
                mAdapter.addAll(object.getInfoSessions());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(getActivity(), "Clicked : " + mAdapter.getItem(position).getCompanyName(),
                Toast.LENGTH_SHORT).show();
    }

}
