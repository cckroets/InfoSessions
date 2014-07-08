package com.sixbynine.infosessions.database;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.infosessions.interfaces.SQLEntity;
import com.sixbynine.infosessions.object.InfoSession;

import java.util.List;

/**
* @author curtiskroetsch
*/
class AsyncAddCompanyDataToDB extends AsyncTask<Object, Void, Void> {

    InfoSession mInfoSession;
    WebData webData;
    ContentValues cv;

    public AsyncAddCompanyDataToDB(Context context, InfoSession infoSession) {
        this.webData = WebData.get(context);
        this.mInfoSession = infoSession;
        this.cv = new ContentValues();
    }

    @Override
    protected Void doInBackground(Object... unused) {
        Log.d("DB", "Adding " + mInfoSession.getWaterlooApiDAO().getEmployer() + " info...");
        addCompanyData();
        updateInfoSessionPermalink();
        insertAll(WebData.FOUNDER_TABLE_NAME, mInfoSession.getCompanyInfo().getFounders());
        insertAll(WebData.NEWS_TABLE_NAME, mInfoSession.getCompanyInfo().getNewsItems());
        insertAll(WebData.WEBSITE_TABLE_NAME, mInfoSession.getCompanyInfo().getWebsites());
        insertAll(WebData.TEAM_TABLE_NAME, mInfoSession.getCompanyInfo().getTeamMembers());
        return null;
    }

    private String getPermalink() {
        return mInfoSession.getCompanyInfo().getPermalink();
    }

    private void addCompanyData() {
        if (notInDatabase(WebData.COMPANY_TABLE_NAME)) {
            insert(WebData.COMPANY_TABLE_NAME, mInfoSession.getCompanyInfo());
        }
        if (notInDatabase(WebData.ADDRESS_TABLE_NAME)) {
            insert(WebData.ADDRESS_TABLE_NAME,
                    new AddressSQL(mInfoSession.getCompanyInfo().getHeadquarters()));
        }
    }

    private void updateInfoSessionPermalink() {
        int sid = mInfoSession.getWaterlooApiDAO().getId();
        String selection = "sid = " + sid;
        cv.clear();
        cv.put("permalink", getPermalink());
        if (webData.getWritableDatabase().update(WebData.INFO_SESSION_TABLE_NAME, cv, selection, null) == WebData.SQL_FAIL) {
            throw new IllegalStateException("Could not update permalink for session: " + sid);
        }
    }

    private <T extends SQLEntity> void insertAll(String table, List<T> objects) {
        if (objects != null && notInDatabase(table)) {
            for (SQLEntity obj : objects) {
                insert(table, obj);
            }
        }
    }


    private void insert(String table, SQLEntity object) {
        ContentValues cv = object.toContentValues();
        cv.put("permalink", getPermalink());
        Log.d("DB", "Inserting into " + table);
        if (webData.getWritableDatabase().insert(table, null, cv) == WebData.SQL_FAIL) {
            Log.e("DB", "insert failed " + table + " " + object.getClass().getSimpleName());
            throw new IllegalStateException("Could not insert " + object.getClass().getSimpleName() + ": "
                    + object.toString());
        }
    }

    /**
     * Test if this company is in the specified table
     *
     * @param table The name of the table
     * @return true only if this company already has entries in the table
     */
    private boolean notInDatabase(String table) {
        return WebData.isNullSelection(webData.getReadableDatabase(), table,
                WebData.getWhereString("permalink", getPermalink()));
    }

}
