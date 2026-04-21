package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for unknown loan types.
 */
public class DefaultLoanApplicationProcessor extends AbstractLoanApplicationProcessor {

    public DefaultLoanApplicationProcessor(PersistenceProvider factory) {
        super(new DefaultLoanInterestCalculator(), factory);
    }

    @Override
    protected String getLoanType() {
        return "DEFAULT";
    }

    @Override
    protected String resolveLoanType(String requestedLoanType) {
        return requestedLoanType;
    }
}
