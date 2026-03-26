package com.bms.domain.controller;

import com.bms.persistence.DAOFactory;

/**
 * Refined bridge abstraction for home loan applications.
 */
public class HomeLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public HomeLoanApplicationProcessor(DAOFactory factory) {
        super(new HomeLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "HOME";
    }
}
