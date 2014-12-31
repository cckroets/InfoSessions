package com.sixbynine.infosessions.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.model.InfoSessionGroup;
import com.sixbynine.infosessions.model.WaterlooInfoSession;

import java.util.ArrayList;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public class InfoSessionsTabsPagerAdapter extends FragmentStatePagerAdapter{

    InfoSessionGroup[] mTabs;
    ArrayList<WaterlooInfoSession> mInfoSessions;

    InfoSessionListFragment[] mListFragments;

    public InfoSessionsTabsPagerAdapter(FragmentManager fm, ArrayList<WaterlooInfoSession> infoSessions){
        super(fm);
        mTabs = InfoSessionGroup.getGroups();
        mInfoSessions = infoSessions;
        mListFragments = new InfoSessionListFragment[mTabs.length];
    }

    @Override
    public Fragment getItem(int position) {
        InfoSessionListFragment frag = InfoSessionListFragment.newInstance(mTabs[position], mInfoSessions);
        mListFragments[position] = frag;
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position].getTitle();
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    public InfoSessionListFragment[] getListFragments(){
        return mListFragments;
    }
}
