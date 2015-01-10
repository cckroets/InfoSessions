package com.sixbynine.infosessions.event.data;

import com.sixbynine.infosessions.model.company.Company;

/**
 * @author curtiskroetsch
 */
public final class CompanyLoadedEvent {

    private Company mCompany;

    public CompanyLoadedEvent(Company company) {
        mCompany = company;
    }

    public Company getData() {
        return mCompany;
    }
}
