package com.sixbynine.infosessions.model.programs;

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
    ITM(Faculty.MATH, "Information Technology Management"),
    MATH_ACCOUNTING(Faculty.MATH, "Accounting"),
    CFM(Faculty.MATH, "Computing & Financial Management"),
    FARM(Faculty.MATH, "Financial Analysis & Risk Management"),
    MATH_BA(Faculty.MATH, "Business Administration"),

    MATH_CA(Faculty.CA, "Chartered Accounting"),


    SYDE(Faculty.ENG, "System Design"),
    SOFTWARE(Faculty.ENG, "Software"),
    COMPUTER(Faculty.ENG, "Computer"),
    MANAGEMENT(Faculty.ENG, "Management"),
    ELECTRIC(Faculty.ENG, "Electrical"),
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

    Program(Faculty faculty, String name){
        this.faculty = faculty;
        this.name = name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public String getName() {
        return name;
    }

    public static Program fromName(String name){
        for(Program p : values()){
            if(p.name().equals(name)){
                return p;
            }
        }
        throw new IllegalArgumentException(name + " is not the name of a Program");
    }
}
