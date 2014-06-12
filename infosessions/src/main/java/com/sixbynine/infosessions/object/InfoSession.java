package com.sixbynine.infosessions.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.object.company.Company;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class InfoSession implements Comparable<InfoSession>, Parcelable {

    public InfoSessionWaterlooApiDAO waterlooApiDAO;
    public Company companyInfo;
    private boolean mFavourite;

    public boolean isFavourite() {
        return mFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        mFavourite = isFavourite;
    }

    @Override
    public int compareTo(InfoSession other) {
        return this.waterlooApiDAO.getStartTime().compareTo(other.waterlooApiDAO.getStartTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        boolean[] booleans = new boolean[3]; //boolean array, first two indicate presence of parcelables
        booleans[0] = waterlooApiDAO != null; //third is just the value of mFavourite
        booleans[1] = companyInfo != null;
        booleans[2] = mFavourite;

        parcel.writeBooleanArray(booleans);
        if (booleans[0]) parcel.writeParcelable(waterlooApiDAO, flags);
        if (booleans[1]) parcel.writeParcelable(companyInfo, flags);
    }

    public static final Creator<InfoSession> CREATOR = new Creator<InfoSession>() {
        @Override
        public InfoSession createFromParcel(Parcel parcel) {
            InfoSession infoSession = new InfoSession();

            boolean[] booleans = new boolean[3];
            parcel.readBooleanArray(booleans);

            if (booleans[0])
                infoSession.waterlooApiDAO = parcel.readParcelable(InfoSessionWaterlooApiDAO.class.getClassLoader());
            if (booleans[1])
                infoSession.companyInfo = parcel.readParcelable(Company.class.getClassLoader());
            infoSession.setFavourite(booleans[3]);
            return infoSession;

        }

        @Override
        public InfoSession[] newArray(int i) {
            return new InfoSession[i];
        }
    };
}
