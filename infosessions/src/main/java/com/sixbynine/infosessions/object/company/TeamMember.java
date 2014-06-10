package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Corresponds to relationships->current_team items in the crunchbase database
 * This might be excessive data, but it's easier to take stuff out than add it
 */
public class TeamMember implements Parcelable, JSONable {

    private String mFirstName;
    private String mLastName;
    private String mTitle;
    private Calendar mStartedOn;
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

    public Calendar getStartedOn() {
        return mStartedOn;
    }

    public void setStartedOn(Calendar startedOn) {
        this.mStartedOn = startedOn;
    }

    public void setStartedOn(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        setStartedOn(c);
    }

    public void setStartedOn(String startedOnString) {
        if (startedOnString == null || "null".equals(startedOnString)) {
            mStartedOn = null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(startedOnString));
                setStartedOn(cal);
            } catch (ParseException e) {
                throw new IllegalArgumentException("invalid date format " + startedOnString);
            }
        }
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
        String[] data = new String[4];
        data[0] = mFirstName;
        data[1] = mLastName;
        data[2] = mPath;
        data[3] = mTitle;
        parcel.writeStringArray(data);
        long[] time = new long[1];
        time[0] = mStartedOn.getTimeInMillis();
        parcel.writeLongArray(time);
    }

    public static final Parcelable.Creator<TeamMember> CREATOR = new Parcelable.Creator<TeamMember>() {

        @Override
        public TeamMember createFromParcel(Parcel parcel) {
            String[] data = new String[4];
            parcel.readStringArray(data);
            long[] time = new long[1];
            parcel.readLongArray(time);

            TeamMember teamMember = new TeamMember();
            teamMember.setFirstName(data[0]);
            teamMember.setLastName(data[1]);
            teamMember.setPath(data[2]);
            teamMember.setTitle(data[3]);
            teamMember.setStartedOn(time[0]);

            return teamMember;
        }

        @Override
        public TeamMember[] newArray(int i) {
            return new TeamMember[i];
        }


    };

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("first_name", mFirstName);
        obj.put("last_name", mLastName);
        obj.put("path", mPath);
        obj.put("title", mTitle);
        if (mStartedOn != null) {
            obj.put("started_on", mStartedOn.getTimeInMillis());
        }
        return obj;
    }

    public static final JSONable.Creator<TeamMember> JSON_CREATOR = new JSONable.Creator<TeamMember>() {
        @Override
        public TeamMember createFromJSONObject(JSONObject obj) throws JSONException {
            TeamMember teamMember = new TeamMember();
            teamMember.setFirstName(obj.getString("first_name"));
            teamMember.setLastName(obj.getString("last_name"));
            teamMember.setPath(obj.getString("path"));
            teamMember.setTitle(obj.getString("title"));
            if (obj.has("started_on")) {
                teamMember.setStartedOn(obj.getLong("started_on"));
            }
            return teamMember;
        }

        @Override
        public TeamMember[] newArray(int size) {
            return new TeamMember[size];
        }
    };
}
