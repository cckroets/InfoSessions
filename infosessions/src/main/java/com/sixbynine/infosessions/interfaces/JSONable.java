package com.sixbynine.infosessions.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parallel interface to Parcelable
 * Should be used with a Creator object
 *
 * @author Steven Kideckel
 */
public interface JSONable {
    /**
     * @return a JSON Object representing the object
     * @throws org.json.JSONException
     */
    public JSONObject toJSON() throws JSONException;

    abstract class Creator<T extends JSONable> {
        public abstract T createFromJSONObject(JSONObject obj) throws JSONException;

        public abstract T[] newArray(int size);

        public JSONArray getJSONArray(List<T> jsonObjs) throws JSONException {
            if (jsonObjs == null) return null;
            JSONArray arr = new JSONArray();
            int len = jsonObjs.size();
            for (int i = 0; i < len; i++) {
                arr.put(jsonObjs.get(i).toJSON());
            }
            return arr;
        }

        public ArrayList<T> createFromJSONArray(JSONArray arr) throws JSONException {
            int len = arr.length();
            ArrayList<T> result = new ArrayList<T>();
            for (int i = 0; i < len; i++) {
                result.add(createFromJSONObject(arr.getJSONObject(i)));
            }
            return result;
        }
    }
}
