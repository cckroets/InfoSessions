package com.sixbynine.infosessions.model;

import java.util.Iterator;
import java.util.Map;

/**
 * @author curtiskroetsch
 */
public final class PermalinkMap implements Iterable<EmployerInfo> {

    private long mLastUpdated;
    private Map<String, EmployerInfo> mMap;

    public PermalinkMap(long lastUpdated, Map<String, EmployerInfo> map) {
        mLastUpdated = lastUpdated;
        mMap = map;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public EmployerInfo getEmployerInfo(String id) {
        return mMap.get(id);
    }

    @Override
    public Iterator<EmployerInfo> iterator() {
        return mMap.values().iterator();
    }
}
