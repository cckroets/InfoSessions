package com.sixbynine.infosessions.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.model.group.InfoSessionGroup;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

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
    private static final String GROUP_KEY = "group";
    private static final String SESSIONS_KEY = "sessions";

    @Inject
    InfoSessionManager mInfoSessionManager;

    @Inject
    InfoSessionPreferenceManager mInfoSessionPreferenceManager;

    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.nothing_text_view)
    TextView mNothingHereTextView;

    InfoSessionListAdapter mAdapter;

    Handler mHandler;

    InfoSessionGroup mGroup;

    Callback mCallback;

    List<WaterlooInfoSession> mAllSessions;

    public interface Callback{
        public void onFavoriteClicked(WaterlooInfoSession infoSession);
        public void onShareClicked(WaterlooInfoSession infoSession);
        public void onTimerClicked(WaterlooInfoSession infoSession);
        public void onDismiss(WaterlooInfoSession infoSession);
        public void onInfoSessionClicked(WaterlooInfoSession infoSession);
    }

    public static InfoSessionListFragment newInstance(InfoSessionGroup group, ArrayList<WaterlooInfoSession> infoSessions){
        InfoSessionListFragment frag = new InfoSessionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GROUP_KEY, group);
        bundle.putParcelableArrayList(SESSIONS_KEY, infoSessions);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof Callback){
            mCallback = (Callback) activity;
        }else{
            throw new IllegalStateException(activity.getClass().getName() + " must implement Callback interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_session_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mGroup = args.getParcelable(GROUP_KEY);
        mAllSessions = args.getParcelableArrayList(SESSIONS_KEY);

        mHandler = new Handler();
        mAdapter = new InfoSessionListAdapter(getActivity(), mGroup.getFilter().filter(mAllSessions), mListView);
        mAdapter.setActionListener(this);
        mListView.setAdapter(mAdapter);
        mNothingHereTextView.setVisibility(mAdapter.getCount() > 0? View.GONE : View.VISIBLE);

    }

    @Override
    public void onFavoriteClicked(WaterlooInfoSession infoSession) {
        mCallback.onFavoriteClicked(infoSession);
    }

    @Override
    public void onShareClicked(WaterlooInfoSession infoSession) {
        mCallback.onShareClicked(infoSession);
    }

    @Override
    public void onTimerClicked(WaterlooInfoSession infoSession) {
        mCallback.onTimerClicked(infoSession);
    }

    @Override
    public void onDismiss(WaterlooInfoSession infoSession) {
        mCallback.onDismiss(infoSession);
    }

    @Override
    public void onInfoSessionClicked(WaterlooInfoSession infoSession) {
        mCallback.onInfoSessionClicked(infoSession);
    }

    public void setDataset(ArrayList<WaterlooInfoSession> sessions){
        mAllSessions = sessions;
    }

    public void setGroup(InfoSessionGroup group){
        mGroup = group;
    }

    public void refreshData(){
        //TODO: Animate Entering and Exiting Cards
        mAdapter.clear();
        mAdapter.addAll(mGroup.getFilter().filter(mAllSessions));
        mAdapter.notifyDataSetChanged();
        mNothingHereTextView.setVisibility(mAdapter.getCount() > 0? View.GONE : View.VISIBLE);
    }

    /*public void updateDisplayState(MainActivity.DisplayState displayState, String query){
        if (isAdded()) {
            Toast.makeText(getActivity(), infoSession.getCompanyName(), Toast.LENGTH_SHORT).show();
            final CompanyInfoFragment fragment = CompanyInfoFragment.createInstance(infoSession);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void updateDisplayState(MainActivity.DisplayState displayState, String query) {
        mAdapter.clear();
        switch (displayState) {
            case UNDISMISSED:
                mAdapter.addAll(mInfoSessionPreferenceManager.getUndismissedInfoSessions(mAllSessions));
                break;
            case DISMISSED:
                mAdapter.addAll(mInfoSessionPreferenceManager.getDismissedInfoSessions(mAllSessions));
                break;
            case QUERY:
                for (WaterlooInfoSession infoSession : mAllSessions) {
                    if (infoSession.getCompanyName().toUpperCase().contains(query.toUpperCase())) {
                        mAdapter.add(infoSession);
                    }
                }
                break;
        }
        mAdapter.notifyDataSetChanged();
    }*/

}
