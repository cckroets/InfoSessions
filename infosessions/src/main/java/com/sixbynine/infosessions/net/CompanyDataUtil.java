package com.sixbynine.infosessions.net;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.infosessions.object.company.Company;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;
import com.sixbynine.infosessions.object.company.Founder;
import com.sixbynine.infosessions.object.company.NewsItem;
import com.sixbynine.infosessions.object.company.TeamMember;
import com.sixbynine.infosessions.object.company.Website;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class CompanyDataUtil {

    private static AsyncCompanyDataFetcher sAsyncCompanyDataFetcher;
    private static String sImageRoot;

    public interface CompanyDataUtilCallback {
        public void onSuccess(InfoSessionWaterlooApiDAO waterlooApiDAO, Company crunchbaseApiDAO);

        public void onFailure(Throwable e);
    }

    public static void getCompanyData(InfoSessionWaterlooApiDAO waterlooApiDAO, CompanyDataUtilCallback callback) {
        if (sAsyncCompanyDataFetcher == null || sAsyncCompanyDataFetcher.getStatus().equals(AsyncTask.Status.FINISHED)) {
            sAsyncCompanyDataFetcher = new AsyncCompanyDataFetcher(waterlooApiDAO);
            sAsyncCompanyDataFetcher.execute(callback);
        }


    }

    private static class CallProcessor implements CrunchbaseApiRestClient.Callback {
        private CompanyDataUtilCallback callback;
        private InfoSessionWaterlooApiDAO waterlooApiDAO;

        public CallProcessor(CompanyDataUtilCallback callback, InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO) {
            this.callback = callback;
            this.waterlooApiDAO = infoSessionWaterlooApiDAO;
        }

        @Override
        public void onSuccess(JSONObject obj) {
            Log.d("InfoSessions", obj.toString());
            try {
                JSONObject metadata = obj.getJSONObject("metadata");
                if(metadata.optString("image_path_prefix").equals("") == false){
                    sImageRoot = metadata.getString("image_path_prefix");
                }

                JSONObject data = obj.getJSONObject("data");

                JSONObject properties = data.getJSONObject("properties");
                String name = properties.optString("name");
                if(name.equals("")) throw new Exception("company name was missing or null!");
                Company company = new Company(name);
                company.setDescription(properties.optString("description"));
                company.setShortDescription(properties.optString("short_description"));
                company.setFoundedDate(properties.optString("founded_on", null));
                company.setPermalink(properties.getString("permalink"));
                company.setHomePageUrl(properties.optString("homepage_url"));


                if(data.has("relationships")){
                    JSONObject relationships = data.getJSONObject("relationships");

                    if(relationships.has("current_team")){
                        JSONObject currentTeam = relationships.getJSONObject("current_team");
                        if(currentTeam.has("items")){
                            JSONArray items = currentTeam.getJSONArray("items");
                            for(int i = 0; i < items.length(); i ++){
                                JSONObject currentTeamMember = items.getJSONObject(i);
                                TeamMember teamMember = new TeamMember();
                                if(currentTeamMember.has("first_name")) teamMember.setFirstName(currentTeamMember.getString("first_name"));
                                if(currentTeamMember.has("last_name")) teamMember.setLastName(currentTeamMember.getString("last_name"));
                                if(currentTeamMember.has("path")) teamMember.setPath(currentTeamMember.getString("path"));
                                if(currentTeamMember.has("started_on")) teamMember.setStartedOn(currentTeamMember.getString("started_on"));
                                company.addTeamMember(teamMember);
                            }
                        }
                    }

                    if(relationships.has("headquarters")){
                        JSONObject headquarters = relationships.getJSONObject("headquarters");
                        if(headquarters.has("items")){
                            JSONArray items = headquarters.getJSONArray("items");
                            if(items.length() > 0){
                                JSONObject hq = items.getJSONObject(0);
                                Address address;
                                String countryCode = hq.optString("country_code");
                                if(countryCode.equals("CAN")){
                                    address = new Address(Locale.CANADA);
                                    address.setCountryName("Canada");
                                }else if(countryCode.equals("USA")){
                                    address = new Address(Locale.US);
                                    address.setCountryName("USA");
                                }else {
                                address = new Address(Locale.US);
                                }

                                address.setAddressLine(0, hq.optString("street_1"));
                                address.setLocality(hq.optString("city"));
                                String adminArea = hq.optString("region");
                                if(adminArea.equals("") || adminArea.equals("null")){
                                    if(address.getLocality() != null && (address.getLocality().equals("Waterloo")
                                                                        || address.getLocality().equals("Toronto")
                                                                        || address.getLocality().equals("Kitchener"))){
                                        address.setAdminArea("Ontario");
                                    }else{
                                        address.setAdminArea(null);
                                    }
                                }else{
                                    address.setAdminArea(adminArea);
                                }
                                address.setCountryCode(hq.optString("country_code"));
                                company.setHeadquarters(address);
                            }
                        }
                    }else if(relationships.has("offices")){
                        JSONObject offices = relationships.getJSONObject("offices");
                        if(offices.has("items")){
                            JSONArray items = offices.getJSONArray("items");
                            if(items.length() > 0){
                                JSONObject hq = items.getJSONObject(0);
                                Address address;
                                if(hq.optString("country_code").equals("CAN")){
                                    address = new Address(Locale.CANADA);
                                }else{
                                    address = new Address(Locale.US);
                                }
                                address.setAddressLine(0, hq.optString("street_1"));
                                address.setLocality(hq.optString("city"));
                                address.setAdminArea(hq.optString("region"));
                                address.setCountryCode(hq.optString("country_code"));
                                company.setHeadquarters(address);
                            }
                        }
                    }

                    if(relationships.has("founders")){
                        JSONObject founders = relationships.getJSONObject("founders");
                        if(founders.has("items")){
                            JSONArray items = founders.getJSONArray("items");
                            for(int i = 0; i < items.length(); i ++){
                                JSONObject item = items.getJSONObject(i);
                                String founderName = item.optString("name");
                                String founderPath = item.optString("path");
                                company.addFounder(new Founder(founderName, founderPath));
                            }
                        }
                    }

                    if(relationships.has("primary_image")){
                        JSONObject primaryImage = relationships.getJSONObject("primary_image");
                        if(primaryImage.has("items")){
                            JSONArray items = primaryImage.getJSONArray("items");
                            if(items.length() > 0){
                                JSONObject item = items.getJSONObject(0);
                                String path = item.optString("path");
                                if(path.equals("") == false){
                                    company.setPrimaryImageUrl(sImageRoot + path);
                                }
                            }
                        }
                    }

                    if(relationships.has("websites")){
                        JSONObject websites = relationships.getJSONObject("websites");
                        if(websites.has("items")){
                            JSONArray items = websites.getJSONArray("items");
                            for(int i = 0; i < items.length(); i ++){
                                JSONObject website = items.getJSONObject(i);
                                company.addWebsite(new Website(website.optString("url"), website.optString("title")));
                            }
                        }
                    }

                    if(relationships.has("news")){
                        JSONObject news = relationships.getJSONObject("news");
                        if(news.has("items")){
                            JSONArray items = news.getJSONArray("items");
                            for(int i = 0; i < items.length(); i ++){
                                JSONObject newsItemJson = items.getJSONObject(i);
                                NewsItem newsItem = new NewsItem();
                                if(newsItemJson.optString("author", null) != null) newsItem.setAuthor(newsItemJson.getString("author"));
                                if(newsItemJson.optString("type", null) != null) newsItem.setType(newsItemJson.getString("type"));
                                if(newsItemJson.optString("title", null) != null) newsItem.setTitle(newsItemJson.getString("title"));
                                if(newsItemJson.optString("posted_on", null) != null) newsItem.setPostDate(newsItemJson.getString("posted_on"));
                                company.addNewsItem(newsItem);
                            }
                        }
                    }

                }
                callback.onSuccess(waterlooApiDAO, company);
            } catch (Exception e) {
                callback.onFailure(e);
            }

        }

        @Override
        public void onFailure(Throwable e) {
            callback.onFailure(e);

        }
    }

    private static class AsyncCompanyDataFetcher extends AsyncTask<CompanyDataUtilCallback, Void, Void> {

        private InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO;

        public AsyncCompanyDataFetcher(InfoSessionWaterlooApiDAO infoSessionWaterlooApiDAO) {
            this.infoSessionWaterlooApiDAO = infoSessionWaterlooApiDAO;
        }

        @Override
        protected Void doInBackground(CompanyDataUtilCallback... callbacks) {
            if (callbacks.length == 0) {
                throw new IllegalArgumentException("Must provide a callback");
            } else if (callbacks[0] == null) {
                throw new IllegalArgumentException("Provided callback is null");
            }
            CompanyDataUtilCallback callback = callbacks[0];
            CrunchbaseApiRestClient.get("/organization/" + infoSessionWaterlooApiDAO.getEmployer(), null, new CallProcessor(callback, infoSessionWaterlooApiDAO));

            return null;
        }
    }
}
