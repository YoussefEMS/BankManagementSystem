package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for home loan applications.
 */
public class HomeLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public HomeLoanApplicationProcessor(PersistenceProvider factory) {
        super(new HomeLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "HOME";
    }
}
