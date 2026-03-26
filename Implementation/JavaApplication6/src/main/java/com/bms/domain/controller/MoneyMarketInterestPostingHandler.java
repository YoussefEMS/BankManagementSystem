package com.bms.domain.controller;

import com.bms.persistence.DAOFactory;

/**
 * Refined bridge abstraction for monthly money market interest posting.
 */
public class MoneyMarketInterestPostingHandler extends AbstractInterestPostingHandler {

    public MoneyMarketInterestPostingHandler(DAOFactory factory) {
        super(new MoneyMarketInterest(), factory);
    }

    @Override
    protected String getAccountType() {
        return "Money Market";
    }
}
