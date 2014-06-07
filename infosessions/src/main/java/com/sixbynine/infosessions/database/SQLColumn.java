package com.sixbynine.infosessions.database;

/**
 * An Abstract SQL Column which is a Name-DataType pair, and can be a primary key.
 *
 * @author curtiskroetsch
 */
public abstract class SQLColumn {
    private final String name;
    private final boolean isPrimary;

    private SQLColumn(String name, boolean isPrimary) {
        this.name = name;
        this.isPrimary = isPrimary;
    }

    /**
     * A declaration of the column in a CREATE TABLE () clause
     * May contain keywords (e.g. PRIMARY, FOREIGN, etc..)
     *
     * @return The declartion of the column
     */
    public abstract String getDeclaration();

    /**
     * Get the name of the Column
     *
     * @return column key
     */
    public String getName() {
        return name;
    }

    /**
     * Test if the Column is a primary key
     *
     * @return true is the column is a primary key
     */
    public boolean isPrimary() {
        return isPrimary;
    }


    /**
     * Creates a new SQL Column (not foreign)
     *
     * @param name    The name of the field
     * @param type    The datatype of the field
     * @param primary True if the field is a primary key
     * @return A new SQL Column
     */
    public static SQLColumn newColumn(final String name, final SQLType type, final boolean primary) {
        return new SQLColumn(name, primary) {
            @Override
            public String getDeclaration() {
                return getName() + " " + type.toString();
            }
        };
    }

    /**
     * Creates a new Column that is a foreign key to another table
     *
     * @param name     The name of the field
     * @param type     The datatype of the field
     * @param primary  True if the field is a primary key
     * @param refTable The foreign table reference
     * @param refName  The name of the key in the referenced table
     * @return A new Foreign Key SQL Column
     */
    static SQLColumn newForeignColumn(final String name, final SQLType type,
                                      final boolean primary, final SQLTable refTable,
                                      final String refName) {
        return new SQLColumn(name, primary) {
            @Override
            public String getDeclaration() {
                return String.format("%s %s REFERENCES %s (%s)", name, type.toString(),
                        refTable.getName(), refName);
            }
        };
    }

}

