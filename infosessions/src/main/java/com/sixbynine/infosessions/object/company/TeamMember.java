package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Corresponds to person items in the crunchbase database
 * This might be excessive data, but it's easier to take stuff out than add it
 */
public class TeamMember implements Parcelable {

    private String mFirstName;
    private String mLastName;
    private String mTitle;
    private String mStartedOn;
    private String mPath;

    public TeamMember() {

    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getStartedOn() {
        return mStartedOn;
    }

    public void setStartedOn(String startedOn) {
        this.mStartedOn = startedOn;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] data = new String[5];
        data[0] = mFirstName;
        data[1] = mLastName;
        data[2] = mPath;
        data[3] = mStartedOn;
        data[4] = mTitle;
        parcel.writeStringArray(data);
    }

    public static final Parcelable.Creator<TeamMember> CREATOR = new Parcelable.Creator<TeamMember>() {

        @Override
        public TeamMember createFromParcel(Parcel parcel) {
            String[] data = new String[5];
            parcel.readStringArray(data);
            TeamMember teamMember = new TeamMember();
            teamMember.setFirstName(data[0]);
            teamMember.setLastName(data[1]);
            teamMember.setPath(data[2]);
            teamMember.setStartedOn(data[3]);
            teamMember.setTitle(data[4]);
            return teamMember;
        }

        @Override
        public TeamMember[] newArray(int i) {
            return new TeamMember[i];
        }


    };
}
