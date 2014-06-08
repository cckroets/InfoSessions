package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

import com.sixbynine.infosessions.interfaces.JSONable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Subclass of ArrayList.  Should be used instead of ArrayList to hold lists of {@link com.sixbynine.infosessions.object.company.Website} objects
 * <p/>
 * Benefits:
 * <ul>
 * <li>1. Implements {@link android.os.Parcelable}</li>
 * <li>2. Has special access methods to search websites by social media type</li>
 * </ul>
 */
public class WebsiteCatalogue extends ArrayList<Website> implements Parcelable, JSONable {
    public static final int HOMEPAGE = 0;
    public static final int FACEBOOK = 1;
    public static final int LINKEDIN = 2;
    public static final int ANGELLIST = 3;
    public static final int TWITTER = 4;
    public static final int INSTAGRAM = 5;
    public static final int PINTEREST = 6;
    public static final int NUM_FIELDS = 7;

    private static final String[] sTitles = {"homepage", "facebook", "linkedin", "angellist", "twitter", "instagram", "pinterest"};

    public WebsiteCatalogue() {
        super();
    }

    public WebsiteCatalogue(int size) {
        super(size);
    }

    public boolean has(int websiteType) {
        return getWebsiteByType(websiteType) != null;
    }

    public Website getWebsiteByType(int websiteType) {
        if (websiteType < HOMEPAGE || websiteType > PINTEREST) {
            throw new IllegalArgumentException("invalid website type: " + websiteType + ", valid values range from " + HOMEPAGE + " to " + PINTEREST);
        }
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i).getTitle().equals(sTitles[websiteType])) {
                return get(i);
            }
        }
        return null;
    }

    /**
     * @return an array of boolean.  The size of the array is equal to {@link com.sixbynine.infosessions.object.company.WebsiteCatalogue#NUM_FIELDS}
     * and each index is true if the catalogue holds that type of social media/website
     */
    public boolean[] getContentArray() {
        boolean[] result = new boolean[NUM_FIELDS];

        for (int i = size() - 1; i >= 0; i--) {
            for (int j = 0; j < NUM_FIELDS; j++) {
                if (get(i).getTitle().equals(sTitles[j])) {
                    result[j] = true;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        int len = size();
        parcel.writeInt(len);
        for (int i = 0; i < len; i++) {
            parcel.writeParcelable(get(i), flags);
        }
    }

    public static final Parcelable.Creator<WebsiteCatalogue> CREATOR = new Parcelable.Creator<WebsiteCatalogue>() {
        @Override
        public WebsiteCatalogue createFromParcel(Parcel parcel) {
            int len = parcel.readInt();
            WebsiteCatalogue catalogue = new WebsiteCatalogue(len);
            for (int i = 0; i < len; i++) {
                Website website = parcel.readParcelable(Website.class.getClassLoader());
                catalogue.add(website);
            }
            return catalogue;
        }

        @Override
        public WebsiteCatalogue[] newArray(int i) {
            return new WebsiteCatalogue[0];
        }
    };

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        int len = size();
        for (int i = 0; i < len; i++) {
            arr.put(get(i).toJSON());
        }
        obj.put("catalogue", arr);
        return obj;
    }

    public static final JSONable.Creator<WebsiteCatalogue> JSON_CREATOR = new JSONable.Creator<WebsiteCatalogue>() {
        @Override
        public WebsiteCatalogue createFromJSONObject(JSONObject obj) throws JSONException {
            JSONArray arr = obj.getJSONArray("catalogue");
            WebsiteCatalogue websites = new WebsiteCatalogue(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonWebsite = arr.getJSONObject(i);
                websites.add(Website.JSON_CREATOR.createFromJSONObject(jsonWebsite));
            }
            return websites;
        }

        @Override
        public WebsiteCatalogue[] newArray(int size) {
            return new WebsiteCatalogue[size];
        }
    };

}
