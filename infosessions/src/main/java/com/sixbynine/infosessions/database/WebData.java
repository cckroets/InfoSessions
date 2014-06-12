package com.sixbynine.infosessions.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;


/**
 * A SQLite Database to store data from the web
 *
 * @author curtiskroetsch
 */
public class WebData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sixbynineInfosessions";
    private static final int DATABASE_VERSION = 2;
    private static final long SQL_FAIL = -1;

    private static final SimpleDateFormat SQL_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * A Table for each employee session and its details
     */
    private static final String INFO_SESSION_TABLE_NAME = "SESSION";
    private static final SQLTable INFO_SESSION_TABLE =
            new SQLTable.Builder(INFO_SESSION_TABLE_NAME)
                    .addPrimaryKey("sid", SQLType.INTEGER)
                    .addKey("employer", SQLType.TEXT)
                    .addKey("startTime", SQLType.DATETIME)
                    .addKey("endTime", SQLType.DATETIME)
                    .addKey("location", SQLType.TEXT)
                    .addKey("website", SQLType.TEXT)
                    .addKey("forCoopStudents", SQLType.BOOLEAN)
                    .addKey("forGraduatingStudents", SQLType.BOOLEAN)
                    .addKey("description", SQLType.TEXT).build();

    /**
     * A Table for each program-session pair
     */
    private static final String PROGRAM_TABLE_NAME = "PROGRAM";
    private static final SQLTable PROGRAM_TABLE =
            new SQLTable.Builder(PROGRAM_TABLE_NAME)
                    .addPrimaryKey("program", SQLType.TEXT)
                    .addPrimaryForeignKey("sid", SQLType.INTEGER, INFO_SESSION_TABLE, "sid").build();


    public WebData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        INFO_SESSION_TABLE.createTable(db);
        PROGRAM_TABLE.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        INFO_SESSION_TABLE.dropTable(db);
        PROGRAM_TABLE.dropTable(db);
        this.onCreate(db);
    }

    private Cursor getInfoSessionCursor() {
        SQLiteDatabase database = getReadableDatabase();
        if (database == null) {
            throw new IllegalStateException("Database could not be opened");
        }
        return database.query(INFO_SESSION_TABLE_NAME, null, null, null, null, null, null);
    }

    public static List<InfoSessionWaterlooApiDAO> readInfoSessionsFromDB(Context context) {
        WebData webData = new WebData(context);
        Cursor cursor = webData.getInfoSessionCursor();
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }
        List<InfoSessionWaterlooApiDAO> waterlooSessions = new ArrayList<InfoSessionWaterlooApiDAO>(cursor.getCount());
        for (int row = 0; row < cursor.getCount(); row++) {
            // TODO: Populate waterlooSessions
        }

        cursor.close();
    }

    public static String calendarToSQL(Calendar cal) {
        return SQL_DATE.format(cal.getTime());
    }


    private static AsyncAddToDB asyncAddToDB;

    /**
     * Save a collection of Info Sessions to the database. This method
     * deals with the case where sessions are already in the database
     * Works asynchronously
     *
     * @param context
     * @param sessions
     */
    public static void saveSessionsToDB(Context context, Collection<InfoSessionWaterlooApiDAO> sessions) {

        asyncAddToDB = new AsyncAddToDB(context, sessions);
        asyncAddToDB.execute();
    }


    private static class AsyncAddToDB extends AsyncTask<Object, Void, Void> {

        private WebData webData;
        private Collection<InfoSessionWaterlooApiDAO> sessions;

        public AsyncAddToDB(Context context, Collection<InfoSessionWaterlooApiDAO> sessions) {
            this.webData = new WebData(context);
            this.sessions = sessions;
        }

        @Override
        protected Void doInBackground(Object... unused) {
            for (InfoSessionWaterlooApiDAO session : sessions) {
                if (isInDatabase(session, webData.getReadableDatabase())) continue;
                boolean success = addInfoSession(session, webData.getWritableDatabase());
                if (!success) {
                    // TODO Handle a failure check for DB insertion
                    webData.close();
                    Log.d("DB", "Problem");
                    throw new IllegalStateException("Could not insert into database: " + session.toString());
                }
            }

            webData.close();
            return null;
        }

        private boolean isInDatabase(InfoSessionWaterlooApiDAO session, SQLiteDatabase database) {
            Cursor cursor = database.query(INFO_SESSION_TABLE_NAME, null, "sid = " + session.getId(),
                    null, null, null, null);

            boolean inDB = (cursor != null) && (cursor.getCount() > 0);
            if (cursor != null)
                cursor.close();
            return inDB;
        }

        /**
         * Add an Info Session to the database
         *
         * @param session to be added
         * @return True if the insertion succeeded
         */
        public boolean addInfoSession(InfoSessionWaterlooApiDAO session, SQLiteDatabase database) {

            ContentValues values = new ContentValues();
            values.put("sid", session.getId());
            values.put("employer", session.getEmployer());
            values.put("startTime", calendarToSQL(session.getStartTime()));
            values.put("endTime", calendarToSQL(session.getEndTime()));
            values.put("location", session.getLocation());
            values.put("website", session.getWebsite());
            values.put("forCoopStudents", session.isForCoopStudents());
            values.put("forGraduatingStudents", session.isForGraduatingStudents());
            values.put("description", session.getDescription());

            if (database.insert(INFO_SESSION_TABLE_NAME, null, values) == SQL_FAIL) {
                return false;
            }

            values.clear();
            values.put("sid", session.getId());
            for (String program : session.getPrograms()) {
                values.put("program", program);
                if (database.insert(PROGRAM_TABLE_NAME, null, values) == SQL_FAIL) {
                    return false;
                }
            }
            return true;
        }
    }
}
