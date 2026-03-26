package com.bms.domain.controller;

import com.bms.persistence.DAOFactory;

/**
 * Refined bridge abstraction for personal loan applications.
 */
public class PersonalLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public PersonalLoanApplicationProcessor(DAOFactory factory) {
        super(new PersonalLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "PERSONAL";
    }
}
