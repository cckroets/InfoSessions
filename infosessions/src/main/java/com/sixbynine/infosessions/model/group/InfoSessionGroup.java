package com.sixbynine.infosessions.model.group;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.WaterlooInfoSessionPreferences;
import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public final class InfoSessionGroup implements Parcelable{
    public static final InfoSessionGroup ALL = new InfoSessionGroup(0, R.string.tab_all, new WaterlooInfoSession.Filter() {
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            return true;
        }
    });
    public static final InfoSessionGroup FAVORITES = new InfoSessionGroup(1, R.string.tab_favorites, new WaterlooInfoSession.Filter() {
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            return p.isFavorited();
        }
    });
    public static final InfoSessionGroup COOP = new InfoSessionGroup(2, R.string.tab_coop, new WaterlooInfoSession.Filter() {
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            return i.isForCoops();
        }
    });
    public static final InfoSessionGroup GRADUATE = new InfoSessionGroup(3, R.string.tab_graduate, new WaterlooInfoSession.Filter() {
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            return i.isForGraduates();
        }
    });
    public static final InfoSessionGroup TODAY = new InfoSessionGroup(4, R.string.tab_today, new WaterlooInfoSession.Filter(){
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            Calendar today = Calendar.getInstance();
            Calendar event = i.getStartTime();
            return today.get(Calendar.YEAR) == event.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == event.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) == event.get(Calendar.DAY_OF_MONTH);
        }
    });
    public static final InfoSessionGroup REMINDERS = new InfoSessionGroup(5, R.string.tab_reminders, new WaterlooInfoSession.Filter() {
        @Override
        public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
            return p.hasAlarm();
        }
    });

    private static final InfoSessionGroup[] CONSTANTS = new InfoSessionGroup[]{ALL, FAVORITES, COOP, GRADUATE, TODAY};

    @IntDef({CONSTANT, PROGRAM, FACULTY})
    public @interface Type{}

    //The type of group, used for serialization
    public static final int CONSTANT = 0;
    public static final int PROGRAM = 1;
    public static final int FACULTY = 2;

    int id;
    String title;
    WaterlooInfoSession.Filter filter;
    Faculty faculty;
    Program program;
    @Type int type;

    /**
     * Constructor for constant groups defined statically in the class
     * @param id the id of the static final InfoGroup
     * @param titleRes the String resource for the title
     * @param filter the filter used to match items
     */
    InfoSessionGroup(int id, int titleRes, WaterlooInfoSession.Filter filter){
        this.id = id;
        this.title = MyApplication.getInstance().getString(titleRes);
        this.filter = filter;
        this.type = CONSTANT;
    }

    /**
     * Constructor for factory-created InfoSession groups
     * @param title the name of the group to be displayed in the tab
     * @param filter the filter used to match items
     * @param type the type of group that this is
     */
     InfoSessionGroup(String title, WaterlooInfoSession.Filter filter, @Type int type){
        this.id = -1;
        this.title = title;
        this.filter = filter;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public WaterlooInfoSession.Filter getFilter() {
        return filter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        switch(type){
            case PROGRAM:
                dest.writeString(program.name());
                break;
            case FACULTY:
                dest.writeString(faculty.name());
                break;
            case CONSTANT:
            default:
                dest.writeInt(id);
                break;
        }
    }



    public static final Creator<InfoSessionGroup> CREATOR = new Creator<InfoSessionGroup>() {
        @Override
        public InfoSessionGroup createFromParcel(Parcel source) {
            int type = source.readInt();
            switch(type){
                case PROGRAM:
                    return createGroupForProgram(Program.fromName(source.readString()));
                case FACULTY:
                    return createGroupForFaculty(Faculty.fromName(source.readString()));
                case CONSTANT:
                    return fromId(source.readInt());
            }
            return null;
        }

        @Override
        public InfoSessionGroup[] newArray(int size) {
            return new InfoSessionGroup[size];
        }
    };

    static InfoSessionGroup fromId(int id){
        for(InfoSessionGroup g : CONSTANTS){
            if(g.id == id){
                return g;
            }
        }
        throw new IllegalArgumentException("Trying to create InfoSessionGroup from invalid id: " + id);
    }

    public static InfoSessionGroup createGroupForFaculty(final Faculty faculty){
        InfoSessionGroup group = new InfoSessionGroup(faculty.name(), new WaterlooInfoSession.Filter() {
            @Override
            public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
                return matchesFaculty(i, faculty);
            }
        }, FACULTY);
        group.faculty = faculty;
        return group;
    }

    public static InfoSessionGroup createGroupForProgram(final Program program){
        InfoSessionGroup group = new InfoSessionGroup(program.name().replaceAll("_", " "), new WaterlooInfoSession.Filter() {
            @Override
            public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
                return (matchesFaculty(i, program.getFaculty()) || matchesProgram(i, program));
            }
        }, PROGRAM);
        group.program = program;
        return group;
    }

    private static boolean matchesFaculty(WaterlooInfoSession i, Faculty faculty){
        return i.getPrograms().contains("ALL - " + faculty.name());
    }

    private static boolean matchesProgram(WaterlooInfoSession i, Program program){
        return i.getPrograms().contains(program.getFaculty().name() + " - " + program.getName());
    }

    public static List<InfoSessionGroup> getGroups(PreferenceManager preferenceManager){
        //TODO allow reordering of tabs

        List<InfoSessionGroup> tabs = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        boolean weekday = c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;


        boolean showToday = weekday && preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_TODAY, true);
        boolean showReminders = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_REMINDERS, false);
        boolean showCoop = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_COOP, true);
        boolean showGrad = preferenceManager.getBoolean(PreferenceManager.Keys.SHOW_GRADUATE, true);


        if(showCoop && showGrad){
            tabs.add(ALL);
            if(showToday) tabs.add(TODAY);
            tabs.add(FAVORITES);
            if(showReminders) tabs.add(REMINDERS);
            tabs.add(COOP);
            tabs.add(GRADUATE);
        }else if(showCoop){
            tabs.add(COOP);
            if(showToday) tabs.add(TODAY);
            tabs.add(FAVORITES);
            if(showReminders) tabs.add(REMINDERS);
        }else if(showGrad){
            tabs.add(GRADUATE);
            if(showToday) tabs.add(TODAY);
            tabs.add(FAVORITES);
            if(showReminders) tabs.add(REMINDERS);
        }else{
            tabs.add(FAVORITES);
            if(showReminders) tabs.add(REMINDERS);
        }

        for(String s : preferenceManager.getStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS)){
            try{
                Program p = Program.fromName(s);
                tabs.add(createGroupForProgram(p));
                continue;
            }catch(Exception e){

            }
            Faculty f = Faculty.fromName(s);
            tabs.add(createGroupForFaculty(f));
        }
        return tabs;
    }
}
