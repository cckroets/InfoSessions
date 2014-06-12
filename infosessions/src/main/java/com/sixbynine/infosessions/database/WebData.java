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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
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
    private static WebData sInstance;

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
                    .addKey("description", SQLType.TEXT)
                    .addKey("permalink", SQLType.TEXT).build();

    /**
     * A Table for each program-session pair
     */
    private static final String PROGRAM_TABLE_NAME = "PROGRAM";
    private static final SQLTable PROGRAM_TABLE =
            new SQLTable.Builder(PROGRAM_TABLE_NAME)
                    .addPrimaryKey("program", SQLType.TEXT)
                    .addPrimaryForeignKey("sid", SQLType.INTEGER, INFO_SESSION_TABLE, "sid").build();


    private static final String COMPANY_TABLE_NAME = "COMPANY";
    private static final SQLTable COMPANY_TABLE =
            new SQLTable.Builder(COMPANY_TABLE_NAME)
            .addPrimaryKey("permalink", SQLType.TEXT)
            .addKey("name", SQLType.TEXT)
            .addKey("description", SQLType.TEXT)
            .addKey("shortDescription", SQLType.TEXT)
            .addKey("foundedDate", SQLType.DATETIME)
            .addKey("primaryImageURL", SQLType.TEXT)
            .addKey("addresss", SQLType.TEXT).build();


    private static final String TEAM_TABLE_NAME = "TEAM_MEMBERS";
    private static final SQLTable TEAM_TABLE =
            new SQLTable.Builder(TEAM_TABLE_NAME)
            .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
            .addPrimaryKey("firstName", SQLType.TEXT)
            .addPrimaryKey("lastName", SQLType.TEXT)
            .addKey("title", SQLType.TEXT)
            .addKey("startDate", SQLType.DATETIME)
            .addKey("path", SQLType.TEXT).build();


    private static final String NEWS_TABLE_NAME = "NEWS_ITEM";
    private static final SQLTable NEWS_TABLE =
            new SQLTable.Builder(NEWS_TABLE_NAME)
            .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
            .addPrimaryKey("url", SQLType.TEXT)
            .addKey("title", SQLType.TEXT)
            .addKey("postDate", SQLType.DATETIME)
            .addKey("author", SQLType.TEXT)
            .addKey("type", SQLType.TEXT).build();


    private static final String WEBSITE_TABLE_NAME = "WEBSITE";
    private static final SQLTable WEBSITE_TABLE =
            new SQLTable.Builder(WEBSITE_TABLE_NAME)
            .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
            .addPrimaryKey("type", SQLType.INTEGER)
            .addKey("url", SQLType.TEXT)
            .addKey("title", SQLType.TEXT).build();


    private static final String FOUNDER_TABLE_NAME = "FOUNDER";
    private static final SQLTable FOUNDER_TABLE =
            new SQLTable.Builder(FOUNDER_TABLE_NAME)
            .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
            .addPrimaryKey("name", SQLType.TEXT)
            .addKey("path", SQLType.TEXT).build();




    private WebData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static WebData get(Context context) {
        if (sInstance == null) {
            sInstance = new WebData(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        INFO_SESSION_TABLE.createTable(db);
        PROGRAM_TABLE.createTable(db);
        COMPANY_TABLE.createTable(db);
        FOUNDER_TABLE.createTable(db);
        NEWS_TABLE.createTable(db);
        TEAM_TABLE.createTable(db);
        WEBSITE_TABLE.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        INFO_SESSION_TABLE.dropTable(db);
        PROGRAM_TABLE.dropTable(db);

        this.onCreate(db);
    }

    private Cursor getCursor(String tableName, String selection) {
        SQLiteDatabase database = getReadableDatabase();
        if (database == null) {
            throw new IllegalStateException("Database could not be opened");
        }
        return database.query(tableName,null,selection,null,null,null,null);
    }

    public static Calendar sqlStringToCalendar(String sqlDate) {
        Calendar cal = new GregorianCalendar();
        try {
            cal.setTime(SQL_DATE.parse(sqlDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("sqlDate is not a valid SQL date");
        }
        return cal;
    }

    public static List<InfoSession> readInfoSessionsFromDB(Context context)
        throws DataNotFoundException {

        WebData webData = WebData.get(context);
        Cursor cursor = webData.getCursor(INFO_SESSION_TABLE_NAME, null);
        if (cursor == null || cursor.getCount() <= 0) {
            throw new DataNotFoundException("No info session data found");
        }
        cursor.moveToFirst();
        List<InfoSession> sessions = new ArrayList<InfoSession>(cursor.getCount());
        for (int row = 0; row < cursor.getCount(); row++) {
            InfoSession session = new InfoSession();


            int id = cursor.getInt(0);
            String employer = cursor.getString(1);
            Calendar startTime = sqlStringToCalendar(cursor.getString(2));
            Calendar endTime = sqlStringToCalendar(cursor.getString(3));
            String location = cursor.getString(4);
            String website = cursor.getString(5);
            boolean forCoops = (cursor.getInt(6) > 0);
            boolean forGrads = (cursor.getInt(7) > 0);
            String description = cursor.getString(8);

            Cursor programCursor = webData.getCursor(PROGRAM_TABLE_NAME, "sid = " + id);
            if (programCursor == null) {
                throw new DataNotFoundException("No program data found");
            }
            List<String> programs = new ArrayList<String>(programCursor.getCount());
            for (programCursor.moveToFirst(); programCursor.moveToNext();) {
                programs.add(programCursor.getString(0));
            }

            InfoSessionWaterlooApiDAO waterlooApi = new InfoSessionWaterlooApiDAO(id);
            waterlooApi.setEmployer(employer);
            waterlooApi.setStartTime(startTime);
            waterlooApi.setEndTime(endTime);
            waterlooApi.setLocation(location);
            waterlooApi.setWebsite(website);
            waterlooApi.setForCoopStudents(forCoops);
            waterlooApi.setForGraduatingStudents(forGrads);
            waterlooApi.setDescription(description);
            waterlooApi.setPrograms(programs);

            session.setWaterlooApiDAO(waterlooApi);
            sessions.add(session);
            cursor.moveToNext();
        }

        cursor.close();

        return sessions;
    }

    public static String calendarToSQL(Calendar cal) {
        return SQL_DATE.format(cal.getTime());
    }


    /**
     * Fill in the company information for the given infoSession.
     *
     * @param infoSession will have its company data overwritten
     * @throws DataNotFoundException if data for company is not presented in database
     */
    public void fillCompanyInfo(InfoSession infoSession)
        throws DataNotFoundException {


        // TODO
        throw  new DataNotFoundException("stub");
    }




    /**
     * Save a collection of Info Sessions to the database. This method
     * deals with the case where sessions are already in the database
     * Works asynchronously
     *
     * @param context
     * @param sessions
     */
    public static void saveSessionsToDB(Context context, Collection<InfoSessionWaterlooApiDAO> sessions) {
        AsyncAddWaterlooInfoToDB asyncAddToDB = new AsyncAddWaterlooInfoToDB(context, sessions);
        asyncAddToDB.execute();
    }

    public static void saveCompanyInfoToDB(Context context, InfoSession infoSession) {
        AsyncAddCompanyDataToDB asyncTask = new AsyncAddCompanyDataToDB(context, infoSession);
        asyncTask.execute();
    }

    private static class AsyncAddWaterlooInfoToDB extends AsyncTask<Object, Void, Void> {

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
         */
        public void addInfoSession(InfoSessionWaterlooApiDAO session, SQLiteDatabase database) {

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
                throw new IllegalStateException("Could not save info session: " + session.toString());
            }

            values.clear();
            values.put("sid", session.getId());
            for (String program : session.getPrograms()) {
                values.put("program", program);
                if (database.insert(PROGRAM_TABLE_NAME, null, values) == SQL_FAIL) {
                    throw new IllegalStateException("Could not save program: " +
                            session.toString() + " for " + program);
                }
            }
        }
    }

    private static class AsyncAddCompanyDataToDB extends AsyncTask<Object, Void, Void> {

        InfoSession mInfoSession;
        WebData webData;

        public AsyncAddCompanyDataToDB(Context context, InfoSession infoSession) {
            this.webData = WebData.get(context);
            this.mInfoSession = infoSession;
        }

        @Override
        protected Void doInBackground(Object... unused) {
            addCompanyData();
            updateInfoSessionPermalink();
            addFounders();
            addNewsItems();
            addTeamMembers();
            addWebsites();
            return null;
        }

        private void addCompanyData() {

        }

        private void updateInfoSessionPermalink() {

        }

        private void addFounders() {

        }

        private void addNewsItems() {

        }

        private void addTeamMembers() {

        }

        private void addWebsites() {

        }


    }
}
