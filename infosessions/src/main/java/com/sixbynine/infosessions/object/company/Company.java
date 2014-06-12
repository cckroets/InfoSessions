package com.sixbynine.infosessions.object.company;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.sixbynine.infosessions.BuildConfig;
import com.sixbynine.infosessions.net.CompanyImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class Company implements Comparable<Company>, Parcelable {


    private String mPermalink;

    private String mHomePageUrl;
    private String mName;
    private String mDescription;
    private String mShortDescription;
    private Calendar mFoundedDate;

    private List<TeamMember> mTeamMembers;
    private String mPrimaryImageUrl;
    private Bitmap mPrimaryImageBitmap;

    private List<NewsItem> mNewsItems;
    private WebsiteCatalogue mWebsiteCatalogue;

    private List<Founder> mFounders;

    private Address mHeadquarters;

    private int mImageStatus;
    public static final int IMAGE_NOT_LOADED = 0;
    public static final int IMAGE_LOADING = 1;
    public static final int IMAGE_LOADED = 2;
    public static final int IMAGE_ERROR_LOADING = 3;

    private OnImageStatusChangedListener mOnImageStatusChangedListener;

    public interface OnImageStatusChangedListener {
        public void onImageStatusChanged(int newStatus);
    }


    /**
     * Constructor for the Company data type
     *
     * @param name the name of the company, this is the unique id for the company
     */
    public Company(String name) {
        mName = name;
        mImageStatus = IMAGE_NOT_LOADED;
    }

    public void setPermalink(String permalink) {
        mPermalink = permalink;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public void setHomePageUrl(String homePageUrl) {
        mHomePageUrl = homePageUrl;
    }

    public String getHomePageUrl() {
        return mHomePageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setFoundedDate(Calendar foundedDate) {
        mFoundedDate = foundedDate;
    }

    public void setFoundedDate(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        setFoundedDate(c);
    }

    /**
     * @param foundedDate should be in the format yyyy-mm-dd
     */
    public void setFoundedDate(String foundedDate) {
        if (foundedDate == null || foundedDate.equals("") || foundedDate.equals("null")) {
            mFoundedDate = null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(foundedDate));
                setFoundedDate(cal);
            } catch (ParseException e) {
                throw new IllegalArgumentException("invalid date format");
            }
        }
    }

    public Calendar getFoundedDate() {
        return mFoundedDate;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        mTeamMembers = teamMembers;
    }

    public void addTeamMember(TeamMember teamMember) {
        if (mTeamMembers == null) {
            mTeamMembers = new ArrayList<TeamMember>();
        }
        mTeamMembers.add(teamMember);
    }

    public TeamMember getTeamMember(int index) {
        if (mTeamMembers == null) {
            return null;
        } else {
            return mTeamMembers.get(index);
        }
    }

    public List<TeamMember> getTeamMembers() {
        return mTeamMembers;
    }

    public void setPrimaryImageUrl(String url) {
        mPrimaryImageUrl = url;
        loadImage();
    }

    public void loadImage() {
        if (mPrimaryImageUrl == null) return;
        mImageStatus = IMAGE_LOADING;
        if (mOnImageStatusChangedListener != null)
            mOnImageStatusChangedListener.onImageStatusChanged(IMAGE_LOADING);
        CompanyImageLoader.getImage(this, new CompanyImageLoader.Callback() {
            @Override
            public void onImageLoaded(Bitmap img) {
                mPrimaryImageBitmap = img;
                mImageStatus = IMAGE_LOADED;
                if (mOnImageStatusChangedListener != null)
                    mOnImageStatusChangedListener.onImageStatusChanged(mImageStatus);
            }

            @Override
            public void onError(Throwable e) {
                if (BuildConfig.DEBUG) Log.e("InfoSessions", e.toString());
                FlurryAgent.onError("InfoSessions", "something", e);
                mImageStatus = IMAGE_ERROR_LOADING;
                if (mOnImageStatusChangedListener != null)
                    mOnImageStatusChangedListener.onImageStatusChanged(mImageStatus);
            }
        });
    }

    public void setOnImageStatusChangedListener(OnImageStatusChangedListener listener) {
        mOnImageStatusChangedListener = listener;
    }

    public String getPrimaryImageUrl() {
        return mPrimaryImageUrl;
    }

    public void setNewsItems(List<NewsItem> newsItems) {
        mNewsItems = newsItems;
    }

    public void addNewsItem(NewsItem newsItem) {
        if (mNewsItems == null) mNewsItems = new ArrayList<NewsItem>();
        mNewsItems.add(newsItem);
    }

    public NewsItem getNewsItem(int index) {
        if (mNewsItems == null) {
            return null;
        } else {
            return mNewsItems.get(index);
        }
    }

    public List<NewsItem> getNewsItems() {
        return mNewsItems;
    }

    public void setWebsites(WebsiteCatalogue websites) {
        mWebsiteCatalogue = websites;
    }

    public void addWebsite(Website website) {
        if (mWebsiteCatalogue == null) mWebsiteCatalogue = new WebsiteCatalogue();
        mWebsiteCatalogue.add(website);
    }

    public Website getWebsiteByIndex(int index) {
        if (mWebsiteCatalogue == null) {
            return null;
        } else {
            return mWebsiteCatalogue.get(index);
        }
    }

    public WebsiteCatalogue getWebsites() {
        return mWebsiteCatalogue;
    }

    public boolean hasWebsite(int websiteType) {
        if (mWebsiteCatalogue == null) {
            return false;
        } else {
            return mWebsiteCatalogue.has(websiteType);
        }
    }

    public Website getWebsiteByType(int websiteType) {
        if (mWebsiteCatalogue == null) {
            return null;
        } else {
            return mWebsiteCatalogue.getWebsiteByType(websiteType);
        }
    }

    public void setFounders(List<Founder> founders) {
        mFounders = founders;
    }

    public void addFounder(Founder founder) {
        if (mFounders == null) {
            mFounders = new ArrayList<Founder>();
        }
        mFounders.add(founder);
    }

    public Founder getFounder(int index) {
        if (mFounders == null) {
            return null;
        } else {
            return mFounders.get(index);
        }
    }

    public int getImageStatus() {
        return mImageStatus;
    }

    public Bitmap getPrimaryImageBitmap() {
        return mPrimaryImageBitmap;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public List<Founder> getFounders() {
        return mFounders;
    }

    public void setHeadquarters(Address headquarters) {
        mHeadquarters = headquarters;
    }

    public Address getHeadquarters() {
        return mHeadquarters;
    }

    @Override
    public int compareTo(Company another) {
        return this.mName.compareTo(another.mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] strings = new String[6];
        strings[0] = mName;
        strings[1] = mPermalink;
        strings[2] = mShortDescription;
        strings[3] = mDescription;
        strings[4] = mHomePageUrl;
        strings[5] = mPrimaryImageUrl;
        parcel.writeStringArray(strings);

        long[] times = new long[1];
        if (mFoundedDate != null) {
            times[0] = mFoundedDate.getTimeInMillis();
        } else {
            times[0] = 0;
        }
        parcel.writeLongArray(times);

        parcel.writeInt(mImageStatus);

        boolean[] parcelablesPresent = new boolean[3];
        parcelablesPresent[0] = mPrimaryImageBitmap != null;
        parcelablesPresent[1] = mWebsiteCatalogue != null;
        parcelablesPresent[2] = mHeadquarters != null;
        if (parcelablesPresent[0]) parcel.writeParcelable(mPrimaryImageBitmap, flags);
        if (parcelablesPresent[1]) parcel.writeParcelable(mWebsiteCatalogue, flags);
        if (parcelablesPresent[2]) parcel.writeParcelable(mHeadquarters, flags);

        boolean[] parcelableArraysPresent = new boolean[3];
        parcelableArraysPresent[0] = mTeamMembers != null;
        parcelableArraysPresent[1] = mNewsItems != null;
        parcelableArraysPresent[2] = mFounders != null;
        parcel.writeBooleanArray(parcelableArraysPresent);

        if (parcelableArraysPresent[0])
            parcel.writeParcelableArray(mTeamMembers.toArray(new TeamMember[mTeamMembers.size()]), flags);
        if (parcelableArraysPresent[1])
            parcel.writeParcelableArray(mNewsItems.toArray(new NewsItem[mNewsItems.size()]), flags);
        if (parcelableArraysPresent[2])
            parcel.writeParcelableArray(mFounders.toArray(new Founder[mFounders.size()]), flags);
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {

        @Override
        public Company createFromParcel(Parcel parcel) {
            String[] strings = new String[6];
            parcel.readStringArray(strings);

            Company company = new Company(strings[0]);
            company.setPermalink(strings[1]);
            company.setShortDescription(strings[2]);
            company.setDescription(strings[3]);
            company.setHomePageUrl(strings[4]);
            company.setPrimaryImageUrl(strings[5]);

            long[] times = new long[1];
            parcel.readLongArray(times);
            if (times[0] == 0) {
                company.setFoundedDate((String) null);
            } else {
                company.setFoundedDate(times[1]);
            }

            company.mImageStatus = parcel.readInt();

            boolean[] parcelablesPresent = new boolean[3];
            parcel.readBooleanArray(parcelablesPresent);
            if (parcelablesPresent[0])
                company.mPrimaryImageBitmap = parcel.readParcelable(Bitmap.class.getClassLoader());
            if (parcelablesPresent[1])
                company.mWebsiteCatalogue = parcel.readParcelable(WebsiteCatalogue.class.getClassLoader());
            if (parcelablesPresent[2])
                company.mHeadquarters = parcel.readParcelable(Address.class.getClassLoader());

            boolean[] parcelableArraysPresent = new boolean[3];
            parcel.readBooleanArray(parcelableArraysPresent);
            if (parcelableArraysPresent[0]) {
                TeamMember[] teamMembers = (TeamMember[]) parcel.readParcelableArray(TeamMember.class.getClassLoader());
                List<TeamMember> teamMemberList = new ArrayList<TeamMember>(teamMembers.length);
                for (TeamMember teamMember : teamMembers) {
                    teamMemberList.add(teamMember);
                }
                company.setTeamMembers(teamMemberList);
            }
            if (parcelableArraysPresent[1]) {
                NewsItem[] newsItems = (NewsItem[]) parcel.readParcelableArray(NewsItem.class.getClassLoader());
                List<NewsItem> newsItemList = new ArrayList<NewsItem>(newsItems.length);
                for (NewsItem newsItem : newsItems) {
                    newsItemList.add(newsItem);
                }
                company.setNewsItems(newsItemList);
            }
            if (parcelableArraysPresent[2]) {
                Founder[] founders = (Founder[]) parcel.readParcelableArray(Founder.class.getClassLoader());
                List<Founder> founderList = new ArrayList<Founder>(founders.length);
                for (Founder founder : founders) {
                    founderList.add(founder);
                }
                company.setFounders(founderList);
            }

            return company;
        }

        @Override
        public Company[] newArray(int i) {
            return new Company[i];
        }
    };

}
