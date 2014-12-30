package com.sixbynine.infosessions.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

@SuppressLint("ValidFragment")
/**
 * @author curtiskroetsch
 */
public class InfoSessionListFragment extends RoboFragment implements
        InfoSessionListAdapter.InfoSessionActionListener{

    @Inject
    InfoSessionManager mInfoSessionManager;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @InjectView(R.id.listView)
    ListView mListView;

    InfoSessionListAdapter mAdapter;

    Handler mHandler;

    List<WaterlooInfoSession> mAllSessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_session_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler();
        mAdapter = new InfoSessionListAdapter(getActivity(), new ArrayList<WaterlooInfoSession>(), mListView);
        mAdapter.setActionListener(this);
        mListView.setAdapter(mAdapter);

        mInfoSessionManager.getWaterlooInfoSessions(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                if (getActivity() != null) {
                    mAllSessions = object.getInfoSessions();
                    updateDisplayState(MainActivity.DisplayState.UNDISMISSED, null);
                }
            }

            @Override
            public void onFailure(Exception error) {

            }
        });
    }

    @Override
    public void onFavoriteClicked(WaterlooInfoSession infoSession) {
        mInfoSessionPreferenceManager.editPreferences(infoSession)
                .toggleFavorited()
                .commit();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShareClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(getActivity(), infoSession.getCompanyName() + " shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(getActivity(), infoSession.getCompanyName() + " timer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(WaterlooInfoSession infoSession) {
        mInfoSessionPreferenceManager.editPreferences(infoSession)
                .toggleDismissed()
                .commit();
        mAdapter.remove(infoSession);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInfoSessionClicked(WaterlooInfoSession infoSession) {
        Toast.makeText(getActivity(), infoSession.getCompanyName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    public void updateDisplayState(MainActivity.DisplayState displayState, String query){
        mAdapter.clear();
        switch (displayState) {
            case UNDISMISSED:
                mAdapter.addAll(mInfoSessionPreferenceManager.getUndismissedInfoSessions(mAllSessions));
                break;
            case DISMISSED:
                mAdapter.addAll(mInfoSessionPreferenceManager.getDismissedInfoSessions(mAllSessions));
                break;
            case QUERY:
                for (WaterlooInfoSession infoSession : mAllSessions){
                    if (infoSession.getCompanyName().toUpperCase().contains(query.toUpperCase())){
                        mAdapter.add(infoSession);
                    }
                }
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
