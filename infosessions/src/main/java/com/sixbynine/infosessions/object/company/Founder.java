package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stevenkideckel on 2014-06-08.
 */
public class Founder implements Parcelable {

    private String mName;
    private String mPath;

    public Founder(String name, String path) {
        mName = name;
        mPath = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] data = new String[2];
        data[0] = mName;
        data[1] = mPath;
        parcel.writeStringArray(data);
    }

    public static final Creator<Founder> CREATOR = new Creator<Founder>() {
        @Override
        public Founder createFromParcel(Parcel parcel) {
            String[] data = new String[2];
            parcel.readStringArray(data);
            return new Founder(data[0], data[1]);
        }

        @Override
        public Founder[] newArray(int i) {
            return new Founder[i];
        }
    };
}
