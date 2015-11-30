package com.sixbynine.infosessions.model;

import java.util.Iterator;
import java.util.Map;

/**
 * @author curtiskroetsch
 */
public final class PermalinkMap implements Iterable<EmployerInfo> {

    private final long mLastUpdated;
    private final Map<String, EmployerInfo> mIdToInfoMap;
    private final Map<String, EmployerInfo> mCompanyNameToInfoMap;

    public PermalinkMap(long lastUpdated, Map<String, EmployerInfo> idMap,
                        Map<String, EmployerInfo> companyMap) {
        mLastUpdated = lastUpdated;
        mIdToInfoMap = idMap;
        mCompanyNameToInfoMap = companyMap;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    /**
     * Returns the EmployerInfo for the info session based off of the session id, then the employer name, returns null
     * if it can't find anything.
     */
    public EmployerInfo getEmployerInfo(WaterlooInfoSession infoSession) {
        EmployerInfo employerInfo = mIdToInfoMap.get(infoSession.getId());
        if (employerInfo == null) {
            employerInfo = mCompanyNameToInfoMap.get(infoSession.getCompanyName());
        }
        return employerInfo;
    }

    @Override
    public Iterator<EmployerInfo> iterator() {
        return mIdToInfoMap.values().iterator();
    }
}
