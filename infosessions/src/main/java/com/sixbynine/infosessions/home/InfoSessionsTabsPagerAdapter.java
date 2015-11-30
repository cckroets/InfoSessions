package com.sixbynine.infosessions.home;

import com.google.inject.Inject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.group.InfoSessionGroup;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;

public final class InfoSessionsTabsPagerAdapter extends FragmentStatePagerAdapter {

    List<InfoSessionGroup> mTabs;
    ArrayList<WaterlooInfoSession> mInfoSessions;

    @Inject
    PreferenceManager mPreferenceManager;

    InfoSessionListFragment[] mListFragments;

    public InfoSessionsTabsPagerAdapter(FragmentManager fm, ArrayList<WaterlooInfoSession> infoSessions) {
        super(fm);
        RoboGuice.getInjector(MyApplication.getInstance()).injectMembersWithoutViews(this);
        mInfoSessions = infoSessions;
        refreshData();
    }

    public void refreshData() {
        mTabs = InfoSessionGroup.getGroups(mPreferenceManager);
        mListFragments = new InfoSessionListFragment[mTabs.size()];
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        InfoSessionListFragment frag = InfoSessionListFragment.newInstance(mTabs.get(position), mInfoSessions);
        mListFragments[position] = frag;
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public InfoSessionListFragment[] getListFragments() {
        return mListFragments;
    }

    public boolean setLoading(boolean loading) {
        if (mListFragments != null && mListFragments.length > 0) {
            for (InfoSessionListFragment mListFragment : mListFragments) {
                if (mListFragment != null) {
                    mListFragment.setLoading(loading);
                }
            }
            return true;
        }
        return false;
    }
}
