package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Corresponds to the "websites" section of the crunchbase api
 * Should be held in a {@link com.sixbynine.infosessions.object.company.WebsiteCatalogue}
 */
public class Website implements Parcelable, Comparable<Website>, JSONable {

    private String mUrl;
    private String mTitle;

    public Website(String url, String title) {
        mUrl = url;
        mTitle = title;
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
    }

    public static final Parcelable.Creator<Website> CREATOR = new Parcelable.Creator<Website>() {
        @Override
        public Website createFromParcel(Parcel parcel) {
            String[] data = new String[2];
            parcel.readStringArray(data);
            return new Website(data[0], data[1]);
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
        return obj;
    }

    public static final JSONable.Creator<Website> JSON_CREATOR = new JSONable.Creator<Website>() {
        @Override
        public Website createFromJSONObject(JSONObject obj) throws JSONException {
            return new Website(obj.getString("url"), obj.getString("title"));
        }

        @Override
        public Website[] newArray(int size) {
            return new Website[size];
        }
    };
}
