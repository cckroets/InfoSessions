package com.sixbynine.infosessions.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenkideckel on 14-12-29.
 */
public class WaterlooInfoSessionPreferences implements Parcelable {

    private String mId;
    private boolean mDismissed;
    private boolean mFavorited;
    private int mAlarm; //minutes preceding the event the alarm is set to, or 0 if no alarm set

    /**
     * @param session the {@link com.sixbynine.infosessions.model.WaterlooInfoSession} that this preferences object
     *                corresponds to
     */
    public WaterlooInfoSessionPreferences(WaterlooInfoSession session) {
        this(session.getId());
    }

    /**
     * @param id the id of the {@link com.sixbynine.infosessions.model.WaterlooInfoSession} that this preferences object
     *           corresponds to
     */
    public WaterlooInfoSessionPreferences(String id) {
        mId = id;
        mDismissed = false;
        mFavorited = false;
        mAlarm = 0;
    }

    private WaterlooInfoSessionPreferences(Parcel in) {
        mId = in.readString();
        mDismissed = in.readInt() == 1;
        mFavorited = in.readInt() == 1;
        mAlarm = in.readInt();
    }

    public String getId() {
        return mId;
    }

    public boolean isDismissed() {
        return mDismissed;
    }

    public boolean isFavorited() {
        return mFavorited;
    }

    public int getAlarm() {
        return mAlarm;
    }

    public boolean hasAlarm() {
        return mAlarm != 0;
    }

    public void setDismissed(boolean mDismissed) {
        this.mDismissed = mDismissed;
    }

    public void setFavorited(boolean mFavorited) {
        this.mFavorited = mFavorited;
    }

    public void setAlarm(int minutes) {
        mAlarm = minutes;
    }

    public void removeAlarm() {
        mAlarm = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mDismissed ? 1 : 0);
        dest.writeInt(mFavorited ? 1 : 0);
        dest.writeInt(mAlarm);
    }

    public static final Creator<WaterlooInfoSessionPreferences> CREATOR = new Creator<WaterlooInfoSessionPreferences>() {
        @Override
        public WaterlooInfoSessionPreferences createFromParcel(Parcel source) {
            return new WaterlooInfoSessionPreferences(source);
        }

        @Override
        public WaterlooInfoSessionPreferences[] newArray(int size) {
            return new WaterlooInfoSessionPreferences[size];
        }
    };
}
