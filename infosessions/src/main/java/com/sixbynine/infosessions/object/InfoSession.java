package com.sixbynine.infosessions.object;

import com.sixbynine.infosessions.object.company.Company;

/**
 * Created by stevenkideckel on 2014-06-06.
 */
public class InfoSession implements Comparable<InfoSession> {

    public InfoSessionWaterlooApiDAO waterlooApiDAO;
    public Company companyInfo;

    @Override
    public int compareTo(InfoSession other) {
        return this.waterlooApiDAO.getStartTime().compareTo(other.waterlooApiDAO.getStartTime());
    }
}
