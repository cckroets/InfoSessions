package com.sixbynine.infosessions.object.company;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by stevenkideckel on 2014-06-08.
 */
public class NewsItem implements Parcelable {

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
        if (postDate == null) {
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
        time[0] = mPostDate.getTimeInMillis();
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
            newsItem.setPostDate(time[0]);

            return newsItem;
        }

        @Override
        public NewsItem[] newArray(int i) {
            return new NewsItem[i];
        }
    };
}