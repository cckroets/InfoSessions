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
 * Corresponds to the data found in the "news" tag
 */
public class NewsItem implements Parcelable, JSONable {

    private String mUrl;
    private String mTitle;
    private Calendar mPostDate;
    private String mAuthor;
    private String mType;

    public NewsItem() {

    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public Calendar getPostDate() {
        return mPostDate;
    }

    public void setPostDate(String postDate) {
        if (postDate == null || postDate.equals("") || postDate.equals("null")) {
            mPostDate = null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(postDate));
                setPostDate(cal);
            } catch (ParseException e) {
                throw new IllegalArgumentException("invalid date format");
            }
        }
    }

    public void setPostDate(Calendar postDate) {
        mPostDate = postDate;
    }

    public void setPostDate(long postDateInMillis) {
        if (postDateInMillis <= 0)
            throw new IllegalArgumentException("given time must be positive");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(postDateInMillis);
        setPostDate(c);
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] data = new String[4];
        data[0] = mAuthor;
        data[1] = mTitle;
        data[2] = mType;
        data[3] = mUrl;
        parcel.writeStringArray(data);

        long[] time = new long[1];
        if (mPostDate != null) {
            time[0] = mPostDate.getTimeInMillis();
        } else {
            time[0] = 0;
        }

        parcel.writeLongArray(time);
    }

    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel parcel) {
            String[] data = new String[4];
            parcel.readStringArray(data);
            long[] time = new long[1];
            parcel.readLongArray(time);

            NewsItem newsItem = new NewsItem();
            newsItem.setAuthor(data[0]);
            newsItem.setTitle(data[1]);
            newsItem.setType(data[2]);
            newsItem.setUrl(data[3]);
            if (time[0] != 0) newsItem.setPostDate(time[0]);

            return newsItem;
        }

        @Override
        public NewsItem[] newArray(int i) {
            return new NewsItem[i];
        }
    };

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("url", mUrl);
        obj.put("title", mTitle);
        obj.put("author", mAuthor);
        obj.put("type", mType);
        if (mPostDate != null) {
            obj.put("postDate", mPostDate.getTimeInMillis());
        }
        return obj;

    }

    public static final JSONable.Creator<NewsItem> JSON_CREATOR = new JSONable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromJSONObject(JSONObject obj) throws JSONException {
            NewsItem newsItem = new NewsItem();
            newsItem.setUrl(obj.getString("url"));
            newsItem.setTitle(obj.getString("title"));
            newsItem.setAuthor(obj.getString("author"));
            newsItem.setType(obj.getString("type"));
            if (obj.has("postDate")) {
                newsItem.setPostDate(obj.getLong("postDate"));
            }
            return newsItem;
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
