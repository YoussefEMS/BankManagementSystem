package com.bms.domain.controller;

import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.DAOFactory;

public class MonthlyInterestHandler {
    private final AbstractInterestPostingHandler savingsHandler;
    private final AbstractInterestPostingHandler moneyMarketHandler;

    public MonthlyInterestHandler() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public MonthlyInterestHandler(DAOFactory factory) {
        this.savingsHandler = new SavingsInterestPostingHandler(factory);
        this.moneyMarketHandler = new MoneyMarketInterestPostingHandler(factory);
    }

    public int postMonthlyInterest() {
        return savingsHandler.postInterest() + moneyMarketHandler.postInterest();
    }
}
