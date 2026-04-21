package com.bms.domain.controller;

import com.bms.persistence.PersistenceProvider;

/**
 * Refined bridge abstraction for monthly money market interest posting.
 */
public class MoneyMarketInterestPostingProcessor extends InterestPostingProcessor {

    public MoneyMarketInterestPostingProcessor(PersistenceProvider factory) {
        super(new MoneyMarketInterest(), factory);
    }

    @Override
    protected String getAccountType() {
        return "Money Market";
    }
}
