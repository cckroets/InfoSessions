package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by stevenkideckel on 2014-06-08.
 */
public class WebsiteCatalogue extends ArrayList<Website> implements Parcelable {
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

    public static final Creator<WebsiteCatalogue> CREATOR = new Creator<WebsiteCatalogue>() {
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
}
