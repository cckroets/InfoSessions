package com.sixbynine.infosessions.object.company;

import android.graphics.Bitmap;
import android.location.Address;
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
public class Company implements Comparable<Company> {


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
        if (foundedDate == null) {
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
        mImageStatus = IMAGE_LOADING;
        CompanyImageLoader.getImage(url, new CompanyImageLoader.Callback(){
            @Override
            public void onImageLoaded(Bitmap img) {
                mPrimaryImageBitmap = img;
                mImageStatus = IMAGE_LOADED;
            }

            @Override
            public void onError(Throwable e) {
                if(BuildConfig.DEBUG) Log.e("InfoSessions", e.toString());
                FlurryAgent.onError("InfoSessions", "something", e);
                mImageStatus = IMAGE_ERROR_LOADING;
            }
        });
    }

    public void loadImage(){
        if(mPrimaryImageUrl == null) return;
        mImageStatus = IMAGE_LOADING;
        CompanyImageLoader.getImage(mPrimaryImageUrl, new CompanyImageLoader.Callback(){
            @Override
            public void onImageLoaded(Bitmap img) {
                mPrimaryImageBitmap = img;
                mImageStatus = IMAGE_LOADED;
            }

            @Override
            public void onError(Throwable e) {
                if(BuildConfig.DEBUG) Log.e("InfoSessions", e.toString());
                FlurryAgent.onError("InfoSessions", "something", e);
                mImageStatus = IMAGE_ERROR_LOADING;
            }
        });
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

    public int getImageStatus(){
        return mImageStatus;
    }

    public Bitmap getPrimaryImageBitmap(){
        return mPrimaryImageBitmap;
    }

    public void setShortDescription(String shortDescription){
        mShortDescription = shortDescription;
    }

    public String getShortDescription(){
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

    //TODO: add code to download the image based off of the url


    @Override
    public int compareTo(Company another) {
        return this.mName.compareTo(another.mName);
    }
}
