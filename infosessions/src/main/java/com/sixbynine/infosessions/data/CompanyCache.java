package com.sixbynine.infosessions.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.util.LruCache;

import com.sixbynine.infosessions.model.company.Company;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class CompanyCache implements Cache<String, Company> {

    private static final int DEFAULT_CACHE_SIZE = 50;

    LruCache<String, Company> mCache;

    @Inject
    private CompanyCache() {
        mCache = new LruCache<>(DEFAULT_CACHE_SIZE);
    }


    @Override
    public void put(String key, Company value) {
        mCache.put(key, value);
    }

    @Override
    public Company get(String key) {
        return mCache.get(key);
    }

    @Override
    public void clear() {
        mCache.evictAll();
    }
}
