package com.sixbynine.infosessions.net;

import com.google.gson.JsonElement;

/**
 * @author curtiskroetsch
 */
public final class JsonUtil {

    private JsonUtil() {

    }

    public static long getLong(JsonElement element) {
        final Number rawNum = element.getAsNumber();
        return rawNum == null ? 0 : rawNum.longValue();
    }
}
