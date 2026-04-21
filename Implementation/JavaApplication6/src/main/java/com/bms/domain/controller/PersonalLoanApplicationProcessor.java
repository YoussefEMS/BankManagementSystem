package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for personal loan applications.
 */
public class PersonalLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public PersonalLoanApplicationProcessor(PersistenceProvider factory) {
        super(new PersonalLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "PERSONAL";
    }
}
