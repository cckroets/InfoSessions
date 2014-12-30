package com.sixbynine.infosessions.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenkideckel on 14-12-29.
 */
public class WaterlooInfoSessionPreferences implements Parcelable{

    private String mId;
    private boolean mDismissed;
    private boolean mFavorited;
    private List<Integer> mAlarmIds;

    /**
     *
     * @param session the {@link com.sixbynine.infosessions.model.WaterlooInfoSession} that this
     *           preferences object corresponds to
     */
    public WaterlooInfoSessionPreferences(WaterlooInfoSession session){
        this(session.getId());
    }

    /**
     *
     * @param id the id of the {@link com.sixbynine.infosessions.model.WaterlooInfoSession} that this
     *           preferences object corresponds to
     */
    public WaterlooInfoSessionPreferences(String id){
        mId = id;
        mDismissed = false;
        mFavorited = false;
        mAlarmIds = new ArrayList<>();
    }

    private WaterlooInfoSessionPreferences(Parcel in){
        mId = in.readString();
        mDismissed = in.readInt() == 1;
        mFavorited = in.readInt() == 1;
        int alarmIdSize = in.readInt();
        mAlarmIds = new ArrayList<>(alarmIdSize);
        for(int i = 0; i < alarmIdSize; i ++){
            mAlarmIds.add(in.readInt());
        }
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

    public List<Integer> getAlarmIds() {
        return mAlarmIds;
    }

    public void setDismissed(boolean mDismissed) {
        this.mDismissed = mDismissed;
    }

    public void setFavorited(boolean mFavorited) {
        this.mFavorited = mFavorited;
    }

    public void setAlarmIds(List<Integer> mAlarmIds) {
        this.mAlarmIds = mAlarmIds;
    }

    public boolean addAlarm(Integer id){
        return mAlarmIds.add(id);
    }

    public boolean removeAlarm(Integer id){
        return mAlarmIds.remove(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mDismissed? 1: 0);
        dest.writeInt(mFavorited? 1 : 0);
        int size = mAlarmIds.size();
        dest.writeInt(size);
        for(int i = 0; i < size; i ++){
            dest.writeInt(mAlarmIds.get(i));
        }
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
