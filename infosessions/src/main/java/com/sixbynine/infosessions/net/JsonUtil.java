package com.sixbynine.infosessions.net;

import com.google.gson.JsonElement;

/**
 * @author curtiskroetsch
 */
public final class JsonUtil {

    private JsonUtil() {

    }

    public static long getLong(JsonElement element) {
        return element == null || element.getAsNumber() == null ? 0 : element.getAsNumber()
                .longValue();
    }

    public static String getString(JsonElement element) {
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }
}
