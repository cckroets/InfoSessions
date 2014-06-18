package com.sixbynine.infosessions.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author curtiskroetsch
 */
public interface SQLEntity {

    public ContentValues toContentValues();

    abstract class Creator<T extends SQLEntity> {

        public abstract T createFromCursor(Cursor cursor);

        public String getString(Cursor cursor, String colName) {
            return cursor.getString(cursor.getColumnIndex(colName));
        }

        public int getInt(Cursor cursor, String colName) {
            return cursor.getInt(cursor.getColumnIndex(colName));
        }
    }


}
