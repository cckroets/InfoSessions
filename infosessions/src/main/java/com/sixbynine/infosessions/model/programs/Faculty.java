package com.sixbynine.infosessions.model.programs;

import com.sixbynine.infosessions.R;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public enum Faculty {

    MATH(0, R.string.mathematics),
    ENG(1, R.string.engineering),
    SCI(2, R.string.science),
    ARTS(3, R.string.arts),
    ENV(4, R.string.environment),
    AHS(5, R.string.applied_health_sciences),
    CA(6, R.string.chartered_accounting);

    int id;
    int nameRes;

    Faculty(int id, int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
    }

    public int getId() {
        return id;
    }

    public int getName() {
        return nameRes;
    }

    public static Faculty fromName(String name) {
        for (Faculty f : values()) {
            if (f.name().equals(name)) {
                return f;
            }
        }
        throw new IllegalArgumentException(name + " is not the name of a Faculty");
    }
}
