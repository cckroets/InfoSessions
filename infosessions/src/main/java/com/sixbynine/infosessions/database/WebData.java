package com.sixbynine.infosessions.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Log;

import com.sixbynine.infosessions.database.sql.SQLTable;
import com.sixbynine.infosessions.database.sql.SQLType;
import com.sixbynine.infosessions.interfaces.SQLEntity;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;
import com.sixbynine.infosessions.object.company.Company;
import com.sixbynine.infosessions.object.company.Founder;
import com.sixbynine.infosessions.object.company.NewsItem;
import com.sixbynine.infosessions.object.company.TeamMember;
import com.sixbynine.infosessions.object.company.Website;
import com.sixbynine.infosessions.object.company.WebsiteCatalogue;

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
    public static final long SQL_FAIL = -1;

    private static final SimpleDateFormat SQL_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static WebData sInstance;

    /**
     * A Table for each employee session and its details
     */
    public static final String INFO_SESSION_TABLE_NAME = "SESSION";
    public static final SQLTable INFO_SESSION_TABLE =
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
    public static final String PROGRAM_TABLE_NAME = "PROGRAM";
    public static final SQLTable PROGRAM_TABLE =
            new SQLTable.Builder(PROGRAM_TABLE_NAME)
                    .addPrimaryKey("program", SQLType.TEXT)
                    .addPrimaryForeignKey("sid", SQLType.INTEGER, INFO_SESSION_TABLE, "sid").build();


    /**
     * Basic Company information
     */
    public static final String COMPANY_TABLE_NAME = "COMPANY";
    public static final SQLTable COMPANY_TABLE =
            new SQLTable.Builder(COMPANY_TABLE_NAME)
                    .addPrimaryKey("permalink", SQLType.TEXT)
                    .addKey("name", SQLType.TEXT)
                    .addKey("description", SQLType.TEXT)
                    .addKey("shortDescription", SQLType.TEXT)
                    .addKey("foundedDate", SQLType.DATETIME)
                    .addKey("primaryImageURL", SQLType.TEXT).build();

    /**
     * Contains the address of the headquarters for each company
     */
    public static final String ADDRESS_TABLE_NAME = "HEADQUARTERS";
    public static final SQLTable ADDRESS_TABLE =
            new SQLTable.Builder(ADDRESS_TABLE_NAME)
                    .addPrimaryKey("permalink", SQLType.TEXT)
                    .addKey("addressLine", SQLType.TEXT)
                    .addKey("locality", SQLType.TEXT)
                    .addKey("adminArea", SQLType.TEXT)
                    .addKey("country", SQLType.TEXT)
                    .addKey("locale", SQLType.INTEGER).build();


    /**
     * Contains information about the team members of a company
     */
    public static final String TEAM_TABLE_NAME = "TEAM_MEMBERS";
    public static final SQLTable TEAM_TABLE =
            new SQLTable.Builder(TEAM_TABLE_NAME)
                    .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
                    .addPrimaryKey("firstName", SQLType.TEXT)
                    .addPrimaryKey("lastName", SQLType.TEXT)
                    .addPrimaryKey("title", SQLType.TEXT)
                    .addKey("startedOn", SQLType.DATETIME)
                    .addKey("path", SQLType.TEXT).build();


    /**
     * Contains news items about a company
     */
    public static final String NEWS_TABLE_NAME = "NEWS_ITEM";
    public static final SQLTable NEWS_TABLE =
            new SQLTable.Builder(NEWS_TABLE_NAME)
                    .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
                    .addPrimaryKey("url", SQLType.TEXT)
                    .addKey("title", SQLType.TEXT)
                    .addKey("postDate", SQLType.DATETIME)
                    .addKey("author", SQLType.TEXT)
                    .addKey("type", SQLType.TEXT).build();


    /**
     * Contains various websites associated with a company
     */
    public static final String WEBSITE_TABLE_NAME = "WEBSITE";
    public static final SQLTable WEBSITE_TABLE =
            new SQLTable.Builder(WEBSITE_TABLE_NAME)
                    .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
                    .addPrimaryKey("type", SQLType.INTEGER)
                    .addKey("url", SQLType.TEXT)
                    .addKey("title", SQLType.TEXT).build();


    /**
     * Contains information about the founders of a company
     */
    public static final String FOUNDER_TABLE_NAME = "FOUNDER";
    public static final SQLTable FOUNDER_TABLE =
            new SQLTable.Builder(FOUNDER_TABLE_NAME)
                    .addPrimaryForeignKey("permalink", SQLType.TEXT, COMPANY_TABLE, "permalink")
                    .addPrimaryKey("name", SQLType.TEXT)
                    .addKey("path", SQLType.TEXT).build();


    private WebData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create a selection string for a particular colname against a string literal
     *
     * @param colname The name of the column to check for selection
     * @param value The string literal to check against
     * @return A selection string that can be used for que
     */
    public static String getWhereString(String colname, String value) {
        return colname + " = " + '\'' + value + '\'';
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
        ADDRESS_TABLE.createTable(db);
        PROGRAM_TABLE.createTable(db);
        COMPANY_TABLE.createTable(db);
        FOUNDER_TABLE.createTable(db);
        NEWS_TABLE.createTable(db);
        TEAM_TABLE.createTable(db);
        WEBSITE_TABLE.createTable(db);
        Log.d("DB", "Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        INFO_SESSION_TABLE.dropTable(db);
        ADDRESS_TABLE.dropTable(db);
        PROGRAM_TABLE.dropTable(db);
        COMPANY_TABLE.dropTable(db);
        FOUNDER_TABLE.dropTable(db);
        NEWS_TABLE.dropTable(db);
        TEAM_TABLE.dropTable(db);
        WEBSITE_TABLE.dropTable(db);
        this.onCreate(db);
    }

    private Cursor getCursor(String tableName, String selection) {
        SQLiteDatabase database = getReadableDatabase();
        if (database == null) {
            throw new IllegalStateException("Database could not be opened");
        }
        return database.query(tableName, null, selection, null, null, null, null);
    }

    public static Calendar sqlStringToCalendar(String sqlDate) {
        Calendar cal = new GregorianCalendar();
        try {
            cal.setTime(SQL_DATE.parse(sqlDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("sqlDate is not a valid SQL date");
        } catch (NullPointerException e) {
            return null;
        }
        return cal;
    }

    public static String calendarToSQL(Calendar cal) {
        return (cal == null) ? null : SQL_DATE.format(cal.getTime());
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

    public static boolean isNullSelection(SQLiteDatabase db, String table, String selection) {
        Cursor cursor = db.query(table, null, selection, null, null, null, null);
        boolean inDB = (cursor != null) && (cursor.getCount() > 0);
        if (cursor != null)
            cursor.close();
        return ! inDB;
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
            for (programCursor.moveToFirst(); programCursor.moveToNext(); ) {
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

    /**
     * Fill in the company information for the given infoSession.
     *
     * @param infoSession will have its company data overwritten
     * @throws DataNotFoundException if data for company is not presented in database
     */
    public void fillCompanyInfo(InfoSession infoSession)
            throws DataNotFoundException {

        String permalink = getCompanyPermalink(infoSession);
        Cursor cursor = getCursor(COMPANY_TABLE_NAME, getWhereString("permalink", permalink));
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) cursor.close();
            throw new DataNotFoundException("Company info not found: " +
                    infoSession.getWaterlooApiDAO().getEmployer());
        }
        Log.d("Info Sessions", "Company Info Found: " + infoSession.getWaterlooApiDAO().getEmployer());
        cursor.moveToFirst();
        Company company = Company.SQL_CREATOR.createFromCursor(cursor);
        cursor.close();
        company.setFounders(getSQLObjects(FOUNDER_TABLE_NAME, permalink, Founder.SQL_CREATOR));
        company.setNewsItems(getSQLObjects(NEWS_TABLE_NAME, permalink, NewsItem.SQL_CREATOR));
        company.setTeamMembers(getSQLObjects(TEAM_TABLE_NAME, permalink, TeamMember.SQL_CREATOR));
        company.setWebsites(getWebsites(permalink));
        company.setHeadquarters(getAddress(permalink));
        infoSession.setCompanyInfo(company);
    }


    private <E extends SQLEntity> List<E> getSQLObjects(String table, String permalink, SQLEntity.Creator<E> creator)
        throws DataNotFoundException {
        Cursor cursor = getCursor(table, getWhereString("permalink", permalink));
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) cursor.close();
            throw new DataNotFoundException(table + " info not found: " + permalink);
        }
        List<E> objects = new ArrayList<E>(cursor.getCount());
        for (cursor.moveToFirst(); cursor.moveToNext();) {
            objects.add(creator.createFromCursor(cursor));
        }
        cursor.close();
        return objects;
    }

    private WebsiteCatalogue getWebsites(String permalink)
            throws DataNotFoundException {
        String table = WEBSITE_TABLE_NAME;
        Cursor cursor = getCursor(table, getWhereString("permalink", permalink));
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) cursor.close();
            throw new DataNotFoundException(table + " info not found: " + permalink);
        }
        WebsiteCatalogue catalogue = new WebsiteCatalogue(cursor.getCount());
        for (cursor.moveToFirst(); cursor.moveToNext();) {
            catalogue.add(Website.SQL_CREATOR.createFromCursor(cursor));
        }
        cursor.close();
        return catalogue;
    }

    private Address getAddress(String permalink)
        throws DataNotFoundException {
        String table = ADDRESS_TABLE_NAME;
        Cursor cursor = getCursor(table, getWhereString("permalink", permalink));
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) cursor.close();
            throw new DataNotFoundException(table + " info not found: " + permalink);
        }
        cursor.moveToFirst();
        AddressSQL addressSQL = AddressSQL.SQL_CREATOR.createFromCursor(cursor);
        cursor.close();
        return addressSQL.getAddress();
    }


    private String getCompanyPermalink(InfoSession infoSession) throws DataNotFoundException {

        Cursor cursor = getCursor(INFO_SESSION_TABLE_NAME, "sid = " + infoSession.getWaterlooApiDAO().getId());
        if (cursor == null || cursor.getCount() <= 0) {
            throw new DataNotFoundException("Permalink could not be found for company: " +
                    infoSession.getWaterlooApiDAO().getEmployer());
        }
        cursor.moveToFirst();
        String permalink = cursor.getString(9);
        cursor.close();
        return permalink;
    }

}
