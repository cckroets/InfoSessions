package com.sixbynine.infosessions.model;

import java.util.Calendar;

/**
 * @author curtiskroetsch
 */
public class WaterlooInfoSession {

    private String mId;
    private String mCompanyName;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mLocation;
    private String mWebsite;
    private boolean mForCoops;
    private boolean mForGraduates;
    private String mPrograms;
    private String mDescription;

    public WaterlooInfoSession(String id, String companyName, Calendar startTime, Calendar endTime, String location, String website, boolean forCoops, boolean forGraduates, String programs, String description) {
        this.mId = id;
        this.mCompanyName = companyName;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mLocation = location;
        this.mWebsite = website;
        this.mForCoops = forCoops;
        this.mForGraduates = forGraduates;
        this.mPrograms = programs;
        this.mDescription = description;
    }

    public String getId() {
        return mId;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public boolean isForCoops() {
        return mForCoops;
    }

    public boolean isForGraduates() {
        return mForGraduates;
    }

    public String getPrograms() {
        return mPrograms;
    }

    public String getDescription() {
        return mDescription;
    }
}
