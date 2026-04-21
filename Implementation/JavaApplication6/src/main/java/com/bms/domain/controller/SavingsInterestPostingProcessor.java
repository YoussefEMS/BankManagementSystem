package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for monthly savings interest posting.
 */
public class SavingsInterestPostingProcessor extends InterestPostingProcessor {

    public SavingsInterestPostingProcessor(PersistenceProvider factory) {
        super(new SavingsInterest(), factory);
    }

    @Override
    protected String getAccountType() {
        return "Savings";
    }
}
