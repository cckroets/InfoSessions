package com.sixbynine.infosessions.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.inject.Inject;
import com.sixbynine.infosessions.BuildConfig;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.InfoSessionPreferenceManager;
import com.sixbynine.infosessions.data.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import roboguice.RoboGuice;

/**
 * @author curtiskroetsch
 */
public class WaterlooInfoSession implements Parcelable, Comparable<WaterlooInfoSession> {

  private static final List<String> IGNORE_COMPANIES = Arrays.asList(
      "No info sessions",
      "Labour Day",
      "Lectures begin",
      "Info sessions begin",
      "Closed info session",
      "Lectures end",
      "Christmas");

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

  private WaterlooInfoSession(Parcel in) {
    String[] s = new String[6];
    in.readStringArray(s);

    mId = s[0];
    mCompanyName = s[1];
    mLocation = s[2];
    mWebsite = s[3];
    mPrograms = s[4];
    mDescription = s[5];

    boolean[] b = new boolean[2];
    in.readBooleanArray(b);

    mForCoops = b[0];
    mForGraduates = b[1];

    long[] l = new long[2];
    in.readLongArray(l);

    mStartTime = Calendar.getInstance();
    mStartTime.setTimeInMillis(l[0]);
    mEndTime = Calendar.getInstance();
    mEndTime.setTimeInMillis(l[1]);
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    String[] s = new String[6];
    s[0] = mId;
    s[1] = mCompanyName;
    s[2] = mLocation;
    s[3] = mWebsite;
    s[4] = mPrograms;
    s[5] = mDescription;
    dest.writeStringArray(s);

    boolean[] b = new boolean[2];
    b[0] = mForCoops;
    b[1] = mForGraduates;
    dest.writeBooleanArray(b);

    long[] l = new long[2];
    l[0] = mStartTime.getTimeInMillis();
    l[1] = mEndTime.getTimeInMillis();
    dest.writeLongArray(l);
  }

  public static final Creator<WaterlooInfoSession> CREATOR = new Creator<WaterlooInfoSession>() {
    @Override
    public WaterlooInfoSession createFromParcel(Parcel source) {
      return new WaterlooInfoSession(source);
    }

    @Override
    public WaterlooInfoSession[] newArray(int size) {
      return new WaterlooInfoSession[size];
    }
  };

  @Override
  public int compareTo(WaterlooInfoSession another) {
    return mStartTime.compareTo(another.mStartTime);
  }


  /**
   * Abstract class that provides the ability to filter a list of {@link com.sixbynine.infosessions.model.WaterlooInfoSession}
   */
  public static abstract class Filter {
    @Inject
    InfoSessionPreferenceManager manager;

    @Inject
    PreferenceManager preferenceManager;

    public Filter() {
      RoboGuice.getInjector(MyApplication.getInstance()).injectMembersWithoutViews(this);
    }

    /**
     * @param i the info session to check
     * @param p the associated user preference for the info session
     * @return true if the info session matches the criteria of the filter, false otherwise
     */
    public abstract boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p);

    public ArrayList<WaterlooInfoSession> filter(List<WaterlooInfoSession> infoSessions) {
      ArrayList<WaterlooInfoSession> result = new ArrayList<>();

      boolean showCoop = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_COOP, true);
      boolean showGrad = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_GRADUATE, true);
      boolean showPast = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_PAST, false);

      for (WaterlooInfoSession i : infoSessions) {
        //higher level filters
        if (!BuildConfig.AUDIENCE_BROKEN && !((showCoop && i.mForCoops) || showGrad && i.mForGraduates)) {
          continue;
        } else if (!showPast) {
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR, 0);
          c.set(Calendar.MINUTE, 0);
          c.set(Calendar.SECOND, 0);
          Date today = c.getTime();
          if (i.mEndTime.getTime().before(today)) {
            continue;
          }
        }

        if (IGNORE_COMPANIES.contains(i.getCompanyName())) {
          continue;
        }

        WaterlooInfoSessionPreferences p = manager.getPreferences(i);
        if (matches(i, p)) {
          result.add(i);
        }
      }
      return result;
    }
  }


}
