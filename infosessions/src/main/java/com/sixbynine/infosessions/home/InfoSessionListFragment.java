package com.sixbynine.infosessions.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.group.InfoSessionGroup;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

@SuppressLint("ValidFragment")
/**
 * @author curtiskroetsch
 */
public final class InfoSessionListFragment extends RoboFragment implements
        InfoSessionListAdapter.InfoSessionActionListener {

    private static final String GROUP_KEY = "group";
    private static final String SESSIONS_KEY = "sessions";

    @InjectView(R.id.listView)
    AbsListView mListView; //this is listview for smaller screens, gridview for larger screens
    @InjectView(R.id.nothing_text_view)
    TextView mNothingHereTextView;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    InfoSessionListAdapter mAdapter;

    Handler mHandler;

    InfoSessionGroup mGroup;

    Callback mCallback;

    List<WaterlooInfoSession> mAllSessions;

    public interface Callback {
        void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession);

        void loadListings(boolean useCache);
    }

    public static InfoSessionListFragment newInstance(InfoSessionGroup group, ArrayList<WaterlooInfoSession> infoSessions) {
        InfoSessionListFragment frag = new InfoSessionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GROUP_KEY, group);
        bundle.putParcelableArrayList(SESSIONS_KEY, infoSessions);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new IllegalStateException(context.getClass().getName() + " must implement Callback interface");
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
        if (mListView instanceof ListView) {
            ((ListView) mListView).addHeaderView(getSpace(view.getContext()));
            ((ListView) mListView).addFooterView(getSpace(view.getContext()));
        } else if (mListView instanceof GridViewWithHeaderAndFooter) {
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
        mNothingHereTextView.setVisibility(mAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCallback.loadListings(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(R.color.accent);
    }

    private static View getSpace(Context context) {
        View space = new View(context);
        space.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics())));
        return space;
    }

    @Override
    public void onInfoSessionEvent(InfoSessionListAdapter.Event event, WaterlooInfoSession infoSession) {
        mCallback.onInfoSessionEvent(event, infoSession);
    }

    public void setDataset(ArrayList<WaterlooInfoSession> sessions) {
        mAllSessions = sessions;
    }

    public void refreshData() {
        //TODO: Animate Entering and Exiting Cards
        mAdapter.clear();
        mAdapter.addAll(mGroup.getFilter().filter(mAllSessions));
        mAdapter.notifyDataSetChanged();
        mNothingHereTextView.setVisibility(mAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setLoading(boolean loading) {
        mSwipeRefreshLayout.setRefreshing(loading);
    }

}
