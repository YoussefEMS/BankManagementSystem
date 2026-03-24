package com.bms.domain.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of InterestCalculator for Savings accounts.
 * Calculates monthly interest at 2.5% annual rate.
 */
public class SavingsInterest implements InterestCalculator {

    private static final double ANNUAL_RATE = 0.025; // 2.5%
    private static final double MONTHLY_RATE = ANNUAL_RATE / 12;

    @Override
    public BigDecimal calculate(BigDecimal balance) {
        // Monthly interest = balance * monthly rate
        return balance.multiply(BigDecimal.valueOf(MONTHLY_RATE))
                     .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public double getRate() {
        return MONTHLY_RATE;
    }
}