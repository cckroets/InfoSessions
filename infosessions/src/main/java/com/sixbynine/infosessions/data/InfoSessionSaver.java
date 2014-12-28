package com.sixbynine.infosessions.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.event.data.CompanyLoadedEvent;
import com.sixbynine.infosessions.event.data.WaterlooDataLoadedEvent;
import com.sixbynine.infosessions.model.EmployerInfo;
import com.sixbynine.infosessions.model.PermalinkMap;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.model.company.Company;
import com.squareup.otto.Subscribe;

import java.util.Map;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class InfoSessionSaver {

    private static final String TAG = InfoSessionSaver.class.getName();
    private static final String KEY_SESSIONS = "info_sessions";
    private static final String KEY_PERMALINKS = "permalinks";

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    @Named("data")
    Gson mGson;

    @Inject
    private InfoSessionSaver() {
        MainBus.get().register(this);
    }

    @Subscribe
    public void onCompanyLoaded(CompanyLoadedEvent event) {
        final Company company = event.getData();
        final String companyJson = mGson.toJson(company, Company.class);
        Log.d(TAG, "saving " + company + ": " + companyJson);
        mPreferenceManager.putString(company.getPermalink(), companyJson);
    }

    @Subscribe
    public void onWaterlooSessionsLoaded(WaterlooDataLoadedEvent event) {
        final WaterlooInfoSessionCollection sessions = event.getData();
        final String sessionsJson = mGson.toJson(sessions, WaterlooInfoSessionCollection.class);
        Log.d(TAG, "saving sessions : " + sessionsJson);
        mPreferenceManager.putString(KEY_SESSIONS, sessionsJson);

        final String permalinkJson = mGson.toJson(event.getPermalinkMap(), PermalinkMap.class);
        Log.d(TAG, "saving permalinks : " + permalinkJson);
        mPreferenceManager.putString(KEY_PERMALINKS, permalinkJson);
    }

    public Company getCompany(String permalink) {
        final String companyJson = mPreferenceManager.getString(permalink);
        Log.d(TAG, "companyJson = " + companyJson);
        if (companyJson == null) {
            return null;
        }
        return mGson.fromJson(companyJson, Company.class);
    }

    public WaterlooInfoSessionCollection getWaterlooSessions() {
        final String sessionsJson = mPreferenceManager.getString(KEY_SESSIONS);
        if (sessionsJson == null) {
            return null;
        }
        Log.d(TAG, "getting sessions : " + sessionsJson);
        return mGson.fromJson(sessionsJson, WaterlooInfoSessionCollection.class);
    }

    public PermalinkMap getPermalinks() {
        final String permalinks = mPreferenceManager.getString(KEY_PERMALINKS);
        if (permalinks == null) {
            return null;
        }
        Log.d(TAG, "getting permalinks : " + permalinks);
        return mGson.fromJson(permalinks, PermalinkMap.class);
    }

}
