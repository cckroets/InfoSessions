package com.sixbynine.infosessions.model;

/**
 * @author curtiskroetsch
 */
public class EmployerInfo {

    String mSessionId;
    String mEmployer;
    String mPermalink;

    public EmployerInfo(String sessionId, String employer, String permalink) {
        mSessionId = sessionId;
        mEmployer = employer;
        mPermalink = permalink;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public String getEmployer() {
        return mEmployer;
    }

    public String getPermalink() {
        return mPermalink;
    }
}
