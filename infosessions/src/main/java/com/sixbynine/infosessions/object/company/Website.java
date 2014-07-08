package com.sixbynine.infosessions.object.company;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;
import com.sixbynine.infosessions.interfaces.SQLEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Corresponds to the "websites" section of the crunchbase api
 * Should be held in a {@link com.sixbynine.infosessions.object.company.WebsiteCatalogue}
 */
public class Website implements Parcelable, Comparable<Website>, JSONable, SQLEntity {

    private String mUrl;
    private String mTitle;
    private int mType;

    public Website(String url, String title, int type) {
        mUrl = url;
        mTitle = title;
        mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the title of the Website.  This could be "homepage", "twitter", "facebook" for example
     */
    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] data = new String[2];
        data[0] = mUrl;
        data[1] = mTitle;
        parcel.writeStringArray(data);
        parcel.writeInt(mType);
    }

    public static final Parcelable.Creator<Website> CREATOR = new Parcelable.Creator<Website>() {
        @Override
        public Website createFromParcel(Parcel parcel) {
            String[] data = new String[2];
            parcel.readStringArray(data);
            return new Website(data[0], data[1], parcel.readInt());
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

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("url", mUrl);
        obj.put("title", mTitle);
        obj.put("type", mType);
        return obj;
    }

    public static final JSONable.Creator<Website> JSON_CREATOR = new JSONable.Creator<Website>() {
        @Override
        public Website createFromJSONObject(JSONObject obj) throws JSONException {
            return new Website(obj.getString("url"), obj.getString("title"), obj.getInt("type"));
        }

        @Override
        public Website[] newArray(int size) {
            return new Website[size];
        }
    };

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("url", mUrl);
        cv.put("title", mTitle);
        cv.put("type", mType);
        return cv;
    }

    public static final SQLEntity.Creator<Website> SQL_CREATOR = new SQLEntity.Creator<Website>() {
        @Override
        public Website createFromCursor(Cursor cursor) {
            // TODO: Handle "type"
            return new Website(getString(cursor, "url"), getString(cursor, "title"), getInt(cursor, "type"));
        }
    };
}
