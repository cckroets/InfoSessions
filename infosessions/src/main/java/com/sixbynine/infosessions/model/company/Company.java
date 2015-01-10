package com.sixbynine.infosessions.model.company;

import java.util.List;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public final class Company {

    private String mPermalink;
    private String mHomePageUrl;
    private String mName;
    private String mDescription;
    private String mShortDescription;
    private long mEmployeeCount;

    private String mPrimaryImageUrl;
    private Address mHeadquarters;
    private List<Website> mWebsites;

    public Company(String permalink, String homePageUrl, String name, String longDesc, String shortDesc, long employeeCount, String primaryImageUrl, Address headquarters, List<Website> websites) {
        this.mPermalink = permalink;
        this.mHomePageUrl = homePageUrl;
        this.mName = name;
        this.mDescription = longDesc;
        this.mShortDescription = shortDesc;
        this.mEmployeeCount = employeeCount;
        this.mPrimaryImageUrl = primaryImageUrl;
        this.mHeadquarters = headquarters;
        this.mWebsites = websites;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public String getHomePageUrl() {
        return mHomePageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public long getEmployeeCount() {
        return mEmployeeCount;
    }

    public String getPrimaryImageUrl() {
        return mPrimaryImageUrl;
    }

    public Address getHeadquarters() {
        return mHeadquarters;
    }

    public List<Website> getWebsites() {
        return mWebsites;
    }
}
