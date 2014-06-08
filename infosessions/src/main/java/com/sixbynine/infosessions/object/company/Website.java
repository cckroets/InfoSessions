package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stevenkideckel on 2014-06-08.
 */
public class Website implements Parcelable, Comparable<Website> {

    private String mUrl;
    private String mType;
    private String mTitle;

    public Website(String url, String title) {
        this(url, title, null);
    }

    public Website(String url, String title, String type) {
        mUrl = url;
        mTitle = title;
        mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] data = new String[3];
        data[0] = mUrl;
        data[1] = mTitle;
        data[2] = mType;
        parcel.writeStringArray(data);
    }

    public static final Creator<Website> CREATOR = new Creator<Website>() {
        @Override
        public Website createFromParcel(Parcel parcel) {
            String[] data = new String[3];
            parcel.readStringArray(data);
            return new Website(data[0], data[1], data[2]);
        }

        @Override
        public Website[] newArray(int i) {
            return new Website[i];
        }
    };

    @Override
    public int compareTo(Website another) {
        return this.getTitle().compareTo(another.getTitle());
    }
}
