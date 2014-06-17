package com.sixbynine.infosessions.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import java.util.Collection;

/**
* @author curtiskroetsch
*/
class AsyncAddWaterlooInfoToDB extends AsyncTask<Object, Void, Void> {

    private WebData webData;
    private Collection<InfoSessionWaterlooApiDAO> sessions;

    public AsyncAddWaterlooInfoToDB(Context context, Collection<InfoSessionWaterlooApiDAO> sessions) {
        this.webData = WebData.get(context);
        this.sessions = sessions;
    }

    @Override
    protected Void doInBackground(Object... unused) {
        for (InfoSessionWaterlooApiDAO session : sessions) {
            if (isInDatabase(session, webData.getReadableDatabase())) continue;
            addInfoSession(session, webData.getWritableDatabase());
        }
        return null;
    }

    private boolean isInDatabase(InfoSessionWaterlooApiDAO session, SQLiteDatabase database) {
        return ! WebData.isNullSelection(database, WebData.INFO_SESSION_TABLE_NAME, "sid = " + session.getId());
    }

    /**
     * Add an Info Session to the database
     *
     * @param session to be added
     */
    public void addInfoSession(InfoSessionWaterlooApiDAO session, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put("sid", session.getId());
        values.put("employer", session.getEmployer());
        values.put("startTime", WebData.calendarToSQL(session.getStartTime()));
        values.put("endTime", WebData.calendarToSQL(session.getEndTime()));
        values.put("location", session.getLocation());
        values.put("website", session.getWebsite());
        values.put("forCoopStudents", session.isForCoopStudents());
        values.put("forGraduatingStudents", session.isForGraduatingStudents());
        values.put("description", session.getDescription());

        if (database.insert(WebData.INFO_SESSION_TABLE_NAME, null, values) == WebData.SQL_FAIL) {
            throw new IllegalStateException("Could not save info session: " + session.toString());
        }

        values.clear();
        values.put("sid", session.getId());
        for (String program : session.getPrograms()) {
            values.put("program", program);
            if (database.insert(WebData.PROGRAM_TABLE_NAME, null, values) == WebData.SQL_FAIL) {
                throw new IllegalStateException("Could not save program: " +
                        session.toString() + " for " + program);
            }
        }
    }
}
