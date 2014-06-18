package com.sixbynine.infosessions.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;

import com.sixbynine.infosessions.interfaces.SQLEntity;

import java.util.Locale;

/**
 * A wrapper class for an Address that implements SQLEntity
 * This is needed to store an Address in a SQLite table
 *
 * @author curtiskroetsch
 */
public class AddressSQL implements SQLEntity {

    private static final int LOCALE_CANADA = 0;
    private static final int LOCALE_US = 1;

    private Address mAddress;

    public AddressSQL(Address address) {
        mAddress = address;
    }

    public Address getAddress() {
        return mAddress;
    }

    private int encodeLocale() {
        if (Locale.CANADA.equals(mAddress.getLocale())) {
            return LOCALE_CANADA;
        } else {
            return LOCALE_US;
        }
    }

    private static Locale decodeLocale(int v) {
        return (v == LOCALE_CANADA) ? Locale.CANADA : Locale.US;
    }

    private static final ContentValues NULL_VALUES = new ContentValues();
    static {
        NULL_VALUES.putNull("addressLine");
        NULL_VALUES.putNull("locality");
        NULL_VALUES.putNull("adminArea");
        NULL_VALUES.putNull("country");
        NULL_VALUES.putNull("locale");
    }

    @Override
    public ContentValues toContentValues() {
        if (mAddress == null) {
            return NULL_VALUES;
        }
        ContentValues cv = new ContentValues();
        cv.put("addressLine", mAddress.getAddressLine(0));
        cv.put("locality", mAddress.getLocality());
        cv.put("adminArea", mAddress.getAdminArea());
        cv.put("country", mAddress.getCountryCode());
        cv.put("locale", encodeLocale());
        return cv;
    }

    public static final Creator<AddressSQL> SQL_CREATOR = new Creator<AddressSQL>() {
        @Override
        public AddressSQL createFromCursor(Cursor cursor) {
            Locale locale = decodeLocale(getInt(cursor, "locale"));
            Address address = new Address(locale);
            address.setAddressLine(0, getString(cursor, "addressLine"));
            address.setAdminArea(getString(cursor, "adminArea"));
            address.setCountryCode(getString(cursor, "country"));
            return new AddressSQL(address);
        }
    };
}
