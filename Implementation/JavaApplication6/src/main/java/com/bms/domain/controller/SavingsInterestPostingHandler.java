package com.bms.domain.controller;

import com.bms.persistence.DAOFactory;

/**
 * Refined bridge abstraction for monthly savings interest posting.
 */
public class SavingsInterestPostingHandler extends AbstractInterestPostingHandler {

    public SavingsInterestPostingHandler(DAOFactory factory) {
        super(new SavingsInterest(), factory);
    }

    @Override
    protected String getAccountType() {
        return "Savings";
    }
}
