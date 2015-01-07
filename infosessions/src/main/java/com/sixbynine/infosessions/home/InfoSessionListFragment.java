package com.sixbynine.infosessions.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import in.srain.cube.views.GridViewWithHeaderAndFooter;
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
    AbsListView mListView; //this is listview for smaller screens, gridview for larger screens
    @InjectView(R.id.nothing_text_view)
    TextView mNothingHereTextView;

    InfoSessionListAdapter mAdapter;

    Handler mHandler;

    InfoSessionGroup mGroup;

    Callback mCallback;

    List<WaterlooInfoSession> mAllSessions;

    public interface Callback{
        public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession);
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

        /*
         * This is a hack to add space at the top of the list/grid view
         * Adding margin to listview causes overscroll makes overscroll look weird,
         * Adding padding to listview causes blank space at top when scrolling
         * Adding padding to first view doesn't work on GridView
         * sorry :(
         */
        if(mListView instanceof ListView){
            ((ListView) mListView).addHeaderView(getSpace(view.getContext()));
            ((ListView) mListView).addFooterView(getSpace(view.getContext()));
        }else if(mListView instanceof GridViewWithHeaderAndFooter){
            ((GridViewWithHeaderAndFooter) mListView).addHeaderView(getSpace(view.getContext()));
            ((GridViewWithHeaderAndFooter) mListView).addFooterView(getSpace(view.getContext()));
        }

        Bundle args = getArguments();
        mGroup = args.getParcelable(GROUP_KEY);
        mAllSessions = args.getParcelableArrayList(SESSIONS_KEY);

        mHandler = new Handler();
        mAdapter = new InfoSessionListAdapter(getActivity(), mGroup.getFilter().filter(mAllSessions));
        mAdapter.setActionListener(this);
        mListView.setAdapter(mAdapter);
        mNothingHereTextView.setVisibility(mAdapter.getCount() > 0? View.GONE : View.VISIBLE);
    }

    private static View getSpace(Context context){
        View space = new View(context);
        space.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics())));
        return space;
    }

    @Override
    public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession) {
        mCallback.onInfoSessionEvent(event, infoSession);
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
