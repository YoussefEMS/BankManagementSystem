package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for auto loan applications.
 */
public class AutoLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public AutoLoanApplicationProcessor(PersistenceProvider factory) {
        super(new AutoLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "AUTO";
    }
}
