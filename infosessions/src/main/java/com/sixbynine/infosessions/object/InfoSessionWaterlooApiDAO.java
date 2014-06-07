package com.sixbynine.infosessions.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Data Access Object to hold the information about an Info Session that
 * was retrieved from the UW API
 */
public class InfoSessionWaterlooApiDAO implements JSONable, Parcelable {
    private int id;
    private String employer;
    private Calendar startTime;
    private Calendar endTime;
    private String location;
    private String website;
    private boolean forCoopStudents;
    private boolean forGraduatingStudents;
    private List<String> programs;
    private String description;

    public InfoSessionWaterlooApiDAO(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isForCoopStudents() {
        return forCoopStudents;
    }

    public void setForCoopStudents(boolean forCoopStudents) {
        this.forCoopStudents = forCoopStudents;
    }

    public boolean isForGraduatingStudents() {
        return forGraduatingStudents;
    }

    public void setForGraduatingStudents(boolean forGraduatingStudents) {
        this.forGraduatingStudents = forGraduatingStudents;
    }

    public List<String> getPrograms() {
        return programs;
    }

    public boolean meetsProgramRequirements(String program) {
        if (program == null) throw new IllegalStateException("program was null");
        if (programs == null) throw new IllegalStateException("programs list not initialized yet");

        for (int i = 0; i < programs.size(); i++) {
            String programs_m = programs.get(i);
            if (programs_m.equals(program)) {
                return true;
            } else if (programs_m.startsWith("ALL") && !program.startsWith("ALL")) {
                String allFaculty = programs_m.split("-")[1].trim();
                String programFaculty = program.split("-")[0].trim();
                if (allFaculty.equals(programFaculty)) {
                    return true;
                }
            }
        }
        return false;

    }

    public void setPrograms(List<String> programs) {
        this.programs = programs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("employer", employer);
        obj.put("startTime", startTime.getTimeInMillis());
        obj.put("endTime", endTime.getTimeInMillis());
        obj.put("location", location);
        obj.put("website", website);
        obj.put("forCoopStudents", forCoopStudents);
        obj.put("forGraduatingStudents", forGraduatingStudents);
        JSONArray programsArr = new JSONArray();
        if (programs != null) {
            int len = programs.size();
            for (int i = 0; i < len; i++) {
                programsArr.put(programs.get(i));
            }
        }
        obj.put("programs", programsArr);
        obj.put("description", description);
        return obj;
    }

    public static final JSONable.Creator<InfoSessionWaterlooApiDAO> JSON_CREATOR = new JSONable.Creator<InfoSessionWaterlooApiDAO>() {

        @Override
        public InfoSessionWaterlooApiDAO createFromJSONObject(JSONObject obj)
                throws JSONException {
            int id = obj.getInt("id");
            InfoSessionWaterlooApiDAO infoSession = new InfoSessionWaterlooApiDAO(id);
            infoSession.setDescription(obj.getString("description"));
            infoSession.setEmployer(obj.getString("employer"));

            Calendar startTime = GregorianCalendar.getInstance();
            startTime.setTimeInMillis(obj.getLong("startTime"));
            infoSession.setStartTime(startTime);

            Calendar endTime = GregorianCalendar.getInstance();
            endTime.setTimeInMillis(obj.getLong("endTime"));
            infoSession.setEndTime(endTime);

            infoSession.setForCoopStudents(obj.getBoolean("forCoopStudents"));
            infoSession.setForGraduatingStudents(obj.getBoolean("forGraduatingStudents"));
            infoSession.setLocation(obj.getString("location"));
            infoSession.setWebsite(obj.getString("website"));

            JSONArray programsArr = obj.getJSONArray("programs");
            List<String> programs = new ArrayList<String>(programsArr.length());
            int len = programsArr.length();
            for (int i = 0; i < len; i++) {
                programs.add(programsArr.getString(i));
            }
            infoSession.setPrograms(programs);

            return infoSession;
        }

        @Override
        public InfoSessionWaterlooApiDAO[] newArray(int size) {
            return new InfoSessionWaterlooApiDAO[size];
        }
    };

    public String toString() {
        try {
            return toJSON().toString();
        } catch (JSONException e) {
            return e.toString();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(employer);
        parcel.writeLong(startTime.getTimeInMillis());
        parcel.writeLong(endTime.getTimeInMillis());
        parcel.writeString(location);
        parcel.writeString(website);
        parcel.writeInt(forCoopStudents ? 1 : 0);
        parcel.writeInt(forGraduatingStudents ? 1 : 0);
        int size = programs.size();
        parcel.writeInt(size);
        for (int i = 0; i < size; i++) {
            parcel.writeString(programs.get(i));
        }
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<InfoSessionWaterlooApiDAO> CREATOR = new Parcelable.Creator<InfoSessionWaterlooApiDAO>() {
        @Override
        public InfoSessionWaterlooApiDAO createFromParcel(Parcel parcel) {
            int id = parcel.readInt();
            InfoSessionWaterlooApiDAO infoSession = new InfoSessionWaterlooApiDAO(id);
            infoSession.setEmployer(parcel.readString());

            Calendar startTime = GregorianCalendar.getInstance();
            startTime.setTimeInMillis(parcel.readLong());
            infoSession.setStartTime(startTime);

            Calendar endTime = GregorianCalendar.getInstance();
            endTime.setTimeInMillis(parcel.readLong());
            infoSession.setEndTime(endTime);

            infoSession.setLocation(parcel.readString());
            infoSession.setWebsite(parcel.readString());
            infoSession.setForCoopStudents(parcel.readInt() == 1);
            infoSession.setForGraduatingStudents(parcel.readInt() == 1);
            int size = parcel.readInt();
            List<String> programs = new ArrayList<String>(size);
            for (int i = 0; i < size; i++) {
                programs.add(parcel.readString());
            }
            infoSession.setPrograms(programs);
            infoSession.setDescription(parcel.readString());

            return infoSession;
        }

        @Override
        public InfoSessionWaterlooApiDAO[] newArray(int size) {
            return new InfoSessionWaterlooApiDAO[size];
        }
    };

}
