package com.sixbynine.infosessions.model.programs;

import com.sixbynine.infosessions.R;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public enum Faculty {

    MATH(0, R.string.mathematics, R.string.mathematics_short),
    ENG(1, R.string.engineering, R.string.engineering_short),
    SCI(2, R.string.science, R.string.science_short),
    ARTS(3, R.string.arts, R.string.arts_short),
    ENV(4, R.string.environment, R.string.environment_short),
    AHS(5, R.string.applied_health_sciences, R.string.applied_health_sciences_short),
    CA(6, R.string.chartered_accounting, R.string.chartered_accounting_short);

    int id;
    int nameRes;
    int shortNameRes;

    Faculty(int id, int nameRes, int shortNameRes){
        this.id = id;
        this.nameRes = nameRes;
        this.shortNameRes = shortNameRes;
    }

    public int getId() {
        return id;
    }

    public int getName() {
        return nameRes;
    }

    public int getShortName() {
        return shortNameRes;
    }

    public static Faculty fromName(String name){
        for(Faculty f : values()){
            if(f.name().equals(name)){
                return f;
            }
        }
        throw new IllegalArgumentException(name + " is not the name of a Faculty");
    }
}
