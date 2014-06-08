package com.sixbynine.infosessions.object.company;

import android.graphics.Bitmap;
import android.location.Address;

import com.sixbynine.infosessions.object.company.NewsItem;
import com.sixbynine.infosessions.object.company.TeamMember;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class Company implements Comparable<Company> {
    private static Map<String, Bitmap> sCompanyImages;

    private String mPermalink;

    private String mHomepageUrl;
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

    @Override
    public int compareTo(Company another) {
        return this.mName.compareTo(another.mName);
    }
}
