package com.sixbynine.infosessions.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public class InfoSessionGroup implements Parcelable{
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

    private static final InfoSessionGroup[] CONSTANTS = new InfoSessionGroup[]{ALL, FAVORITES, COOP, GRADUATE};

    @IntDef({CONSTANT, PROGRAM, FACULTY})
    public @interface Type{}

    //The type of group, used for serialization
    private static final int CONSTANT = 0;
    private static final int PROGRAM = 1;
    private static final int FACULTY = 2;

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
    private InfoSessionGroup(int id, int titleRes, WaterlooInfoSession.Filter filter){
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
    private InfoSessionGroup(String title, WaterlooInfoSession.Filter filter, @Type int type){
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
                    int id = source.readInt();
                    for(InfoSessionGroup g : CONSTANTS){
                        if(g.id == id){
                            return g;
                        }
                    }
            }
            return null;
        }

        @Override
        public InfoSessionGroup[] newArray(int size) {
            return new InfoSessionGroup[size];
        }
    };

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
        InfoSessionGroup group = new InfoSessionGroup(program.name(), new WaterlooInfoSession.Filter() {
            @Override
            public boolean matches(WaterlooInfoSession i, WaterlooInfoSessionPreferences p) {
                return matchesFaculty(i, program.getFaculty()) || matchesProgram(i, program);
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

    public static InfoSessionGroup[] getGroups(){
        return new InfoSessionGroup[]{
                ALL,
                FAVORITES,
                COOP,
                GRADUATE,
                //TODO use different defaults, let user modify
                //I've added these just to demonstrate how we can use the class
                createGroupForProgram(Program.CS),
                createGroupForFaculty(Faculty.MATH),
                createGroupForProgram(Program.ACTSCI),
                createGroupForFaculty(Faculty.ENG),
                createGroupForFaculty(Faculty.ARTS),
                createGroupForFaculty(Faculty.ENV),
        };
    }
}
