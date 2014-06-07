package com.sixbynine.infosessions.database;


import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * @author curtiskroetsch
 */
public class SQLTable {
    final String tableName;
    List<SQLColumn> columns;

    public String getName() {
        return tableName;
    }

    private SQLTable(Builder builder) {
        this.tableName = builder.tableName;
        this.columns = builder.columns;
    }

    public String createTableCommand() {

        StringBuilder creator = new StringBuilder("CREATE TABLE " + tableName + "(");
        StringBuilder primaryKeys = new StringBuilder();

        int added = 0;
        for (SQLColumn column : columns) {
            creator.append(added == 0 ? "" : ", ")
                    .append(column.getDeclaration());
            added++;
            if (column.isPrimary()) {
                primaryKeys.append(primaryKeys.length() == 0 ? "" : ", ")
                        .append(column.getName());
            }
        }

        if (primaryKeys.length() > 0) {
            creator.append(", PRIMARY KEY (")
                    .append(primaryKeys.toString())
                    .append(")");
        }

        creator.append(")");
        return creator.toString();
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL(createTableCommand());
    }

    public String dropTableCommand() {
        return "DROP TABLE IF EXISTS " + getName();
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL(dropTableCommand());
    }


    /**
     * A Builder class for SQL Tables
     */
    public static class Builder {
        private final String tableName;
        List<SQLColumn> columns;

        public Builder(String name) {
            this.tableName = name;
            this.columns = new LinkedList<SQLColumn>();
        }

        public Builder addKey(String name, SQLType type) {
            columns.add(SQLColumn.newColumn(name, type, false));
            return this;
        }

        public Builder addPrimaryKey(String name, SQLType type) {
            columns.add(SQLColumn.newColumn(name, type, true));
            return this;
        }

        public Builder addForeignKey(String name, SQLType type, SQLTable refTable, String refName) {
            columns.add(SQLColumn.newForeignColumn(name, type, false, refTable, refName));
            return this;
        }

        public Builder addPrimaryForeignKey(String name, SQLType type, SQLTable refTable, String refName) {
            columns.add(SQLColumn.newForeignColumn(name, type, true, refTable, refName));
            return this;
        }

        public SQLTable build() {
            return new SQLTable(this);
        }

    }

}
