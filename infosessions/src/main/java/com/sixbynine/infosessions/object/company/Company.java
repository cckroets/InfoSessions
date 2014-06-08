package com.sixbynine.infosessions.object.company;

import android.graphics.Bitmap;
import android.location.Address;

import com.sixbynine.infosessions.object.company.NewsItem;
import com.sixbynine.infosessions.object.company.TeamMember;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class Company implements Comparable<Company> {
    private static Map<String, Bitmap> sCompanyImages;

    private String mPermalink;

    private String mHomePageUrl;
    private String mName;
    private String mDescription;
    private Calendar mFoundedDate;

    private List<TeamMember> mTeamMembers;
    private String mPrimaryImageUrl;
    private Bitmap mPrimaryImageBitmap;

    private List<NewsItem> mNewsItems;
    private WebsiteCatalogue mWebsiteCatalogue;

    private List<Founder> mFounders;

    private Address mHeadquarters;


    /**
     * Constructor for the Company data type
     *
     * @param name the name of the company, this is the unique id for the company
     */
    public Company(String name) {
        mName = name;
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
        if (mPrimaryImageBitmap != null) {
            addUrlImagePair(mPrimaryImageUrl, mPrimaryImageBitmap);
        }
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

    public void addFounders(Founder founder) {
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

    public List<Founder> getFounders() {
        return mFounders;
    }

    public void setHeadquarters(Address headquarters) {
        mHeadquarters = headquarters;
    }

    public Address getHeadquarters() {
        return mHeadquarters;
    }

    public void setPrimaryImageBitmap(Bitmap bitmap) {
        mPrimaryImageBitmap = bitmap;
        if (mPrimaryImageUrl != null) {
            addUrlImagePair(mPrimaryImageUrl, mPrimaryImageBitmap);
        }
    }

    private static void addUrlImagePair(String url, Bitmap image) {
        if (sCompanyImages == null) sCompanyImages = new HashMap<String, Bitmap>();
        sCompanyImages.put(url, image);
    }

    private static Bitmap getCompanyBitmap(String url) {
        if (sCompanyImages == null) return null;
        else return sCompanyImages.get(url);
    }

    //TODO: add code to download the image based off of the url


    @Override
    public int compareTo(Company another) {
        return this.mName.compareTo(another.mName);
    }
}
