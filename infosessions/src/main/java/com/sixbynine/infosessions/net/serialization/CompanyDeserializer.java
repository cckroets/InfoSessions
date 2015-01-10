package com.sixbynine.infosessions.net.serialization;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sixbynine.infosessions.model.company.Address;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.model.company.Website;
import com.sixbynine.infosessions.net.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author curtiskroetsch
 */
public class CompanyDeserializer implements JsonDeserializer<Company> {

    private static final String ITEMS = "items";
    private static final String PAGING = "paging";
    private static final String TYPE = "type";
    private static final String COUNT = "total_items";

    @Override
    public Company deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        try {
            final JsonObject jsonObject = (JsonObject) json;
            final JsonObject data = jsonObject.getAsJsonObject("data");
            final JsonObject properties = data.getAsJsonObject("properties");
            final JsonObject relationships = data.getAsJsonObject("relationships");

            // Properties
            final String permalink = JsonUtil.getString(properties.get("permalink"));
            final String longDesc = JsonUtil.getString(properties.get("description"));
            final String shortDesc = JsonUtil.getString(properties.get("short_description"));
            final String url = JsonUtil.getString(properties.get("homepage_url"));
            final String name = JsonUtil.getString(properties.get("name"));
            final long employeeCount = JsonUtil.getLong(properties.get("number_of_employees"));

            // Relationships
            final String primaryImageUrl = deserializePrimaryImageUrl(relationships.getAsJsonObject("primary_image"));
            final Address address = deserializeAddress(relationships.getAsJsonObject("headquarters"));
            final List<Website> websites = deserializeWebsites(relationships.getAsJsonObject("websites"));

            return new Company(permalink, url, name, longDesc, shortDesc, employeeCount,
                    primaryImageUrl, address, websites);
        } catch (NullPointerException | JsonParseException e) {
            e.printStackTrace();
            Log.e(CompanyDeserializer.class.getName(), e.getMessage());
            return null;
        }


    }


    /**
     * Get the first JsonObject in a paging array of another.
     *
     * @param jsonObject With the form: { "paging" : {...}, "items" : [ {...}, {...}, ..., {...} ] }
     * @return The first jsonObject in the given array
     */
    public JsonObject getFirstItem(JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        final JsonObject paging = jsonObject.getAsJsonObject(PAGING);
        final int totalItems = paging.get(COUNT).getAsNumber().intValue();
        if (totalItems <= 0) {
            return null;
        }

        final JsonArray items = jsonObject.getAsJsonArray(ITEMS);
        return items.get(0).getAsJsonObject();
    }

    /**
     * Similar to getFirstItem, howevers grabs the String value from the first object of the key
     *
     * @param jsonObject   A paging object
     * @param requiredType The object must have a string field "type" that equals "requiredType"
     * @param key          of the desired value.
     * @return
     */
    public String getFirstStringItem(JsonObject jsonObject, String requiredType, String key) {
        final JsonObject firstItem = getFirstItem(jsonObject);
        if (firstItem == null) {
            return null;
        }

        final String type = firstItem.get(TYPE).getAsString();
        if (!TextUtils.equals(requiredType, type)) {
            return null;
        }
        return firstItem.get(key).getAsString();
    }

    public String deserializePrimaryImageUrl(JsonObject primaryImage) {
        return getFirstStringItem(primaryImage, "ImageAsset", "path");
    }

    public Address deserializeAddress(JsonObject address) {
        final JsonObject firstAddress = getFirstItem(address);
        if (firstAddress == null) {
            return null;
        }
        final String country = JsonUtil.getString(firstAddress.get("country"));
        final String region = JsonUtil.getString(firstAddress.get("region"));
        final String city = JsonUtil.getString(firstAddress.get("city"));
        return new Address(city, region, country);
    }

    public Website deserializeWebsite(JsonObject websiteJson) {
        final String url = JsonUtil.getString(websiteJson.get("url"));
        final String title = JsonUtil.getString(websiteJson.get("title"));
        return new Website(url, title);
    }

    public List<Website> deserializeWebsites(JsonObject websitesJson) {
        if (websitesJson == null) {
            return new ArrayList<>();
        }

        final JsonArray websiteArray = websitesJson.getAsJsonArray(ITEMS);
        final int count = websiteArray == null ? 0 : websiteArray.size();
        final List<Website> websites = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            final JsonObject websiteJson = websiteArray.get(i).getAsJsonObject();
            websites.add(deserializeWebsite(websiteJson));
        }

        return websites;
    }
}
