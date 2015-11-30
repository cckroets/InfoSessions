package com.sixbynine.infosessions.model.programs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stevenkideckel on 14-12-30.
 */
public enum Program {

    CS(Faculty.MATH, "Computer Science"),
    AMATH(Faculty.MATH, "Applied Mathematics"),
    PMATH(Faculty.MATH, "Pure Mathematics"),
    STAT(Faculty.MATH, "Statistics"),
    ACTSCI(Faculty.MATH, "Actuarial Science"),
    MATH_FINANCE(Faculty.MATH, "Mathematical Finance"),
    MATH_ECON(Faculty.MATH, "Mathematical Economics"),
    MATH_BUS(Faculty.MATH, "Math & Business"),
    MATH_ITM(Faculty.MATH, "Information Technology Management"),
    MATH_ACCOUNTING(Faculty.MATH, "Accounting"),
    CFM(Faculty.MATH, "Computing & Financial Management"),
    FARM(Faculty.MATH, "Financial Analysis & Risk Management"),
    MATH_BA(Faculty.MATH, "Business Administration"),

    MATH_CA(Faculty.CA, "Chartered Accounting"),


    SYDE(Faculty.ENG, "System Design"),
    SOFTWARE(Faculty.ENG, "Software"),
    COMPUTER(Faculty.ENG, "Computer"),
    MANAGEMENT(Faculty.ENG, "Management"),
    ELECTRICAL(Faculty.ENG, "Electrical"),
    MECHANICAL(Faculty.ENG, "Mechanical"),
    NANO(Faculty.ENG, "Nanotechnology"),
    TRON(Faculty.ENG, "Mechatronics"),
    CIVIL(Faculty.ENG, "Civil"),
    CHEMICAL(Faculty.ENG, "Chemical"),
    ARCH(Faculty.ENG, "Architecture"),

    PHYSICS(Faculty.SCI, "Physics"),
    SCIBUS(Faculty.SCI, "Science & Business"),

    AFM(Faculty.ARTS, "Financial Management"),
    ARBUS(Faculty.ARTS, "Arts & Business");


    Faculty faculty;
    String name;

    Program(Faculty faculty, String name) {
        this.faculty = faculty;
        this.name = name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a map of each faculty to a sorted list of the programs available
     *
     * @return a map of each faculty to the programs in the faculty
     */
    public static Map<Faculty, ArrayList<Program>> getFacultyMap() {
        Map<Faculty, ArrayList<Program>> map = new HashMap<>();
        for (Faculty f : Faculty.values()) {
            map.put(f, new ArrayList<Program>());
        }
        for (Program p : values()) {
            map.get(p.faculty).add(p);
        }
        for (Faculty f : Faculty.values()) {
            Collections.sort(map.get(f), new Comparator<Program>() {
                @Override
                public int compare(Program lhs, Program rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
        return map;
    }

    public static Program fromName(String name) {
        for (Program p : values()) {
            if (p.name().equals(name)) {
                return p;
            }
        }
        throw new IllegalArgumentException(name + " is not the name of a Program");
    }
}
