package com.sixbynine.infosessions.model.company;

/**
 * Corresponds to the "websites" section of the Crunchbase API.
 */
public class Website {

    private String mUrl;
    private SocialMedia mType;

    public Website(String url, String title) {
        mUrl = url;
        try {
            mType = SocialMedia.valueOf(title);
        } catch (IllegalArgumentException e) {
            mType = null;
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public SocialMedia getType() {
        return mType;
    }
}
