package com.sixbynine.infosessions.database;

/**
 * @author curtiskroetsch
 */
public enum SQLType {
    INTEGER("INTEGER"),
    TEXT("TEXT"),
    DATETIME("DATETIME"),
    BOOLEAN("BOOLEAN");

    private final String type;

    SQLType(String s) {
        this.type = s;
    }

    @Override
    public String toString() {
        return type;
    }
}
