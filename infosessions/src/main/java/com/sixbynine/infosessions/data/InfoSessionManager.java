package com.sixbynine.infosessions.data;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sixbynine.infosessions.event.MainBus;
import com.sixbynine.infosessions.event.data.CompanyLoadedEvent;
import com.sixbynine.infosessions.event.data.WaterlooDataLoadedEvent;
import com.sixbynine.infosessions.model.PermalinkMap;
import com.sixbynine.infosessions.model.WaterlooInfoSessionCollection;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.net.CrunchbaseAPI;
import com.sixbynine.infosessions.net.PermalinkAPI;
import com.sixbynine.infosessions.net.WaterlooAPI;
import com.sixbynine.infosessions.util.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class InfoSessionManager {

    private static final String TAG = InfoSessionManager.class.getName();

    @Inject
    PermalinkAPI mPermalinkAPI;

    @Inject
    WaterlooAPI mWaterlooAPI;

    @Inject
    CrunchbaseAPI mCrunchbaseAPI;

    @Inject
    CompanyCache mCompanyCache;

    @Inject
    InfoSessionSaver mInfoSessionDBManager;

    PermalinkMap mPermalinks;

    WaterlooInfoSessionCollection mWaterlooInfoSessionCollection;

    WaterlooSessionsCallback mWaterlooSessionsCallback = new WaterlooSessionsCallback();

    @Inject
    private InfoSessionManager(WaterlooAPI waterlooAPI) {
        MainBus.get().register(this);
        mWaterlooAPI = waterlooAPI;
        loadData();
    }

    private void loadData() {
        mWaterlooAPI.getInfoSessions(mWaterlooSessionsCallback);
    }

    public void getWaterlooInfoSessions(final ResponseHandler<WaterlooInfoSessionCollection> callback) {
        WaterlooInfoSessionCollection collection = mWaterlooInfoSessionCollection;
        if (collection != null) {
            callbackSuccess(callback, collection);
            return;
        }

        collection = mInfoSessionDBManager.getWaterlooSessions();
        if (collection != null) {
            mPermalinks = mInfoSessionDBManager.getPermalinks();
            mWaterlooInfoSessionCollection = collection;
            callbackSuccess(callback, collection);
            return;
        }

        mWaterlooSessionsCallback.addCallback(callback);
    }

    public void getCompanyFromSession(final String sessionId, final ResponseHandler<Company> callback) {
        if (mPermalinks != null) {
            Logger.d("Looking up permalink for sesions %s", sessionId);
            final String permalink = mPermalinks.getEmployerInfo(sessionId).getPermalink();
            getCompanyData(permalink, callback);
            return;
        }
        mWaterlooSessionsCallback.addCallback(new ResponseHandler<WaterlooInfoSessionCollection>() {
            @Override
            public void onSuccess(WaterlooInfoSessionCollection object) {
                getCompanyFromSession(sessionId, callback);
            }

            @Override
            public void onFailure(Exception error) {
                callbackFailure(callback, error);
            }
        });
    }

    public void getCompanyData(String permalink, final ResponseHandler<Company> callback) {
        if (permalink == null) {
            Log.e(TAG, "getCompanyData called with null permalink");
            callback.onFailure(new IllegalArgumentException("permalink == null"));
            return;
        }
        Company company = mCompanyCache.get(permalink);
        if (company != null) {
            callbackSuccess(callback, company);
            return;
        }

        company = mInfoSessionDBManager.getCompany(permalink);
        if (company != null) {
            callbackSuccess(callback, company);
            mCompanyCache.put(permalink, company);
            return;
        }

        loadCompanyData(permalink, callback);
    }

    private void loadCompanyData(final String permalink, final ResponseHandler<Company> callback) {
        mCrunchbaseAPI.getOrganization(permalink, new Callback<Company>() {
            @Override
            public void success(Company company, Response response) {
                callbackSuccess(callback, company);
                MainBus.get().post(new CompanyLoadedEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                callbackFailure(callback, error);
                Log.e(TAG, error.getMessage() != null ? error.getMessage() : "loading " +
                        permalink + " unknown failure");
            }
        });
    }


    private <T> void callbackSuccess(ResponseHandler<T> callback, T object) {
        if (callback != null) {
            callback.onSuccess(object);
        }
    }

    private <T> void callbackFailure(ResponseHandler<T> callback, Exception exception) {
        if (callback != null) {
            callback.onFailure(exception);
        }
    }

    private class WaterlooSessionsCallback implements Callback<WaterlooInfoSessionCollection> {

        List<ResponseHandler<WaterlooInfoSessionCollection>> mCallbacks = new ArrayList<>();

        public void addCallback(ResponseHandler<WaterlooInfoSessionCollection> callback) {
            mCallbacks.add(callback);
        }

        @Override
        public void success(final WaterlooInfoSessionCollection collection, Response response) {
            Log.d(TAG, "Waterloo SUCCESS " + collection.getInfoSessions().get(0).getCompanyName());

            mPermalinkAPI.getPermalinks(new Callback<PermalinkMap>() {
                @Override
                public void success(PermalinkMap permalinkMap, Response response) {
                    Log.d(TAG, "Permalink SUCCESS ");
                    MainBus.get().post(new WaterlooDataLoadedEvent(collection, permalinkMap));
                    mWaterlooInfoSessionCollection = collection;
                    mPermalinks = permalinkMap;
                    for (ResponseHandler<WaterlooInfoSessionCollection> callback : mCallbacks) {
                        callbackSuccess(callback, collection);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Permalink FAILURE " + error.getMessage());
                    WaterlooSessionsCallback.this.failure(error);
                }
            });
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(TAG, "Waterloo FAILURE " + error.getMessage());
            for (ResponseHandler<WaterlooInfoSessionCollection> callback : mCallbacks) {
                callbackFailure(callback, error);
            }
        }
    }

}
