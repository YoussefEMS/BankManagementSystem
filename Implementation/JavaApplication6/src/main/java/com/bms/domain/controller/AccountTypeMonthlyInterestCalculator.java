package com.bms.domain.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.domain.entity.MonthlyInterestContext;
import com.bms.domain.controller.MonthlyInterestCalculator;

public class AccountTypeMonthlyInterestCalculator implements MonthlyInterestCalculator {
    private static final Logger logger = Logger.getLogger(AccountTypeMonthlyInterestCalculator.class.getName());
    
    // Interest rates by account type (annual rates)
    private static final Map<String, Double> ANNUAL_RATES = new HashMap<>();
    
    static {
        ANNUAL_RATES.put("SAVINGS", 4.5);          // 4.5% annual
        ANNUAL_RATES.put("CURRENT", 0.0);          // No interest
        ANNUAL_RATES.put("MONEY_MARKET", 6.0);     // 6.0% annual
        ANNUAL_RATES.put("INVESTMENT", 7.5);       // 7.5% annual
    }

    private static final double MINIMUM_BALANCE_FOR_INTEREST = 1000.0;
    private static final double MAXIMUM_BALANCE_CAP = 10000000.0;

    @Override
    public double getMonthlyInterestRate(String accountType, MonthlyInterestContext context) {
        double annualRate = ANNUAL_RATES.getOrDefault(accountType, 0.0);
        // Convert annual rate to monthly
        return annualRate / 12 / 100;
    }

    @Override
    public double calculateInterest(double currentBalance, double interestRate, String accountType, MonthlyInterestContext context) {
        // Validate balance is within acceptable range
        if (currentBalance < 0) {
            logger.warning("Negative balance detected: " + currentBalance);
            return 0.0;
        }

        if (currentBalance > MAXIMUM_BALANCE_CAP) {
            currentBalance = MAXIMUM_BALANCE_CAP;
            logger.info("Balance capped at maximum: " + MAXIMUM_BALANCE_CAP);
        }

        // Calculate interest: Principal × Rate × Time (time = 1 month = 1/12 year, already done in rate)
        double interestAmount = currentBalance * interestRate;

        // Round to 2 decimal places (cents)
        interestAmount = Math.round(interestAmount * 100.0) / 100.0;

        logger.info("Calculated interest: Balance=" + currentBalance + 
                   ", Rate=" + interestRate + ", Interest=" + interestAmount);

        return interestAmount;
    }

    @Override
    public boolean isEligibleForInterest(String accountType, MonthlyInterestContext context) {
        // CURRENT accounts don't earn interest
        if ("CURRENT".equals(accountType)) {
            return false;
        }

        // Other account types are eligible
        logger.info("Account type " + accountType + " is eligible for interest");
        return true;
    }
}
