package com.sixbynine.infosessions.net.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sixbynine.infosessions.model.EmployerInfo;
import com.sixbynine.infosessions.model.PermalinkMap;
import com.sixbynine.infosessions.net.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author curtiskroetsch
 */
public class PermalinkDeserializer implements JsonDeserializer<PermalinkMap> {

  @Override
  public PermalinkMap deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) {

    final JsonObject jsonObject = (JsonObject) json;

    final JsonObject meta = jsonObject.getAsJsonObject("meta");
    final JsonArray data = jsonObject.getAsJsonArray("data");

    final long lastUpdated = JsonUtil.getLong(meta.get("last_updated"));
    final Map<String, EmployerInfo> idMap = new HashMap<>();
    final Map<String, EmployerInfo> companyMap = new HashMap<>();

    for (JsonElement element : data) {
      final JsonObject employerInfo = element.getAsJsonObject();
      final String id = employerInfo.get("id").getAsString();
      final String employer = employerInfo.get("employer").getAsString();
      final JsonElement pJson = employerInfo.get("permalink");
      final String permalink = pJson.isJsonNull() ? null : pJson.getAsString();
      final EmployerInfo info = new EmployerInfo(id, employer, permalink);
      idMap.put(id, info);
      companyMap.put(employer, info);
    }

    return new PermalinkMap(lastUpdated, idMap, companyMap);
  }
}
