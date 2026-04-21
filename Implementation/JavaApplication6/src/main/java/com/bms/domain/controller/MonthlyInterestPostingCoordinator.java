package com.bms.domain.controller;

import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;

public class MonthlyInterestPostingCoordinator {
    private final InterestPostingProcessor savingsHandler;
    private final InterestPostingProcessor moneyMarketHandler;

    public MonthlyInterestPostingCoordinator() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public MonthlyInterestPostingCoordinator(PersistenceProvider factory) {
        this.savingsHandler = new SavingsInterestPostingProcessor(factory);
        this.moneyMarketHandler = new MoneyMarketInterestPostingProcessor(factory);
    }

    public int postMonthlyInterest() {
        return savingsHandler.postInterest() + moneyMarketHandler.postInterest();
    }
}
