package com.sixbynine.infosessions.object.company;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;
import com.sixbynine.infosessions.interfaces.SQLiteable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Corresponds to the data values found in the Founders tags
 */
public class Founder implements Parcelable, JSONable, SQLiteable {

    private String mName;
    private String mPath;

    public Founder(String name, String path) {
        mName = name;
        mPath = path;
    }

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
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

    public static final Parcelable.Creator<Founder> CREATOR = new Parcelable.Creator<Founder>() {
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

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", mName);
        obj.put("path", mPath);
        return obj;
    }

    public static final JSONable.Creator<Founder> JSON_CREATOR = new JSONable.Creator<Founder>() {
        @Override
        public Founder createFromJSONObject(JSONObject obj) throws JSONException {
            return new Founder(obj.getString("name"), obj.getString("path"));
        }

        @Override
        public Founder[] newArray(int size) {
            return new Founder[size];
        }
    };


    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("name", mName);
        cv.put("path", mPath);
        return cv;
    }

    public static final SQLiteable.Creator<Founder> SQL_CREATOR = new SQLiteable.Creator<Founder>() {
        @Override
        public Founder createFromCursor(Cursor cursor) {
            return new Founder(getString(cursor, "name"), getString(cursor, "path"));
        }
    };
}
