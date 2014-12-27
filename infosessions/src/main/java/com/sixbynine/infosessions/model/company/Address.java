package com.sixbynine.infosessions.model.company;

/**
 * @author curtiskroetsch
 */
public class Address {

    private String mCity;
    private String mRegion;
    private String mCountry;

    public Address(String city, String region, String country) {
        mCity = city;
        mRegion = region;
        mCountry = country;
    }

    public String getCity() {
        return mCity;
    }

    public String getRegion() {
        return mRegion;
    }

    public String getCountry() {
        return mCountry;
    }
}
