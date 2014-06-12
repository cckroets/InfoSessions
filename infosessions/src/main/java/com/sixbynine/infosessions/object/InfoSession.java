package com.sixbynine.infosessions.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.object.company.Company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class InfoSession implements Comparable<InfoSession>, Parcelable {

    private InfoSessionWaterlooApiDAO mWaterlooApiDAO;
    private Company mCompanyInfo;
    private boolean mFavourite;
    private List<OnDataLoadedListener> mOnDataLoadedListeners;

    public interface OnDataLoadedListener {
        public void onDataLoaded(InfoSession infoSession);
    }

    public void setWaterlooApiDAO(InfoSessionWaterlooApiDAO waterlooApiDAO) {
        this.mWaterlooApiDAO = waterlooApiDAO;
    }

    public InfoSessionWaterlooApiDAO getWaterlooApiDAO() {
        return mWaterlooApiDAO;
    }

    public void setCompanyInfo(Company mCompanyInfo) {
        this.mCompanyInfo = mCompanyInfo;
        if (mCompanyInfo != null) notifyListeners();
    }

    public Company getCompanyInfo() {
        return mCompanyInfo;
    }

    public void addOnDataLoadedListener(OnDataLoadedListener l) {
        if (mOnDataLoadedListeners == null)
            mOnDataLoadedListeners = new ArrayList<OnDataLoadedListener>();
        mOnDataLoadedListeners.add(l);
    }

    public void removeOnDataLoadedListener(OnDataLoadedListener l) {
        if (mOnDataLoadedListeners == null) return;
        mOnDataLoadedListeners.remove(l);
    }

    private void notifyListeners() {
        if (mOnDataLoadedListeners != null) {
            for (OnDataLoadedListener l : mOnDataLoadedListeners) {
                l.onDataLoaded(this);
            }
        }
    }

    public boolean isFavourite() {
        return mFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        mFavourite = isFavourite;
    }

    @Override
    public int compareTo(InfoSession other) {
        return this.mWaterlooApiDAO.getStartTime().compareTo(other.mWaterlooApiDAO.getStartTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        boolean[] booleans = new boolean[3]; //boolean array, first two indicate presence of parcelables
        booleans[0] = mWaterlooApiDAO != null; //third is just the value of mFavourite
        booleans[1] = mCompanyInfo != null;
        booleans[2] = mFavourite;

        parcel.writeBooleanArray(booleans);
        if (booleans[0]) parcel.writeParcelable(mWaterlooApiDAO, flags);
        if (booleans[1]) parcel.writeParcelable(mCompanyInfo, flags);
    }

    public static final Creator<InfoSession> CREATOR = new Creator<InfoSession>() {
        @Override
        public InfoSession createFromParcel(Parcel parcel) {
            InfoSession infoSession = new InfoSession();

            boolean[] booleans = new boolean[3];
            parcel.readBooleanArray(booleans);

            if (booleans[0])
                infoSession.mWaterlooApiDAO = parcel.readParcelable(InfoSessionWaterlooApiDAO.class.getClassLoader());
            if (booleans[1])
                infoSession.mCompanyInfo = parcel.readParcelable(Company.class.getClassLoader());
            infoSession.setFavourite(booleans[3]);
            return infoSession;

        }

        @Override
        public InfoSession[] newArray(int i) {
            return new InfoSession[i];
        }
    };
}
