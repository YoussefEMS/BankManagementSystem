package com.bms.domain.controller;

/**
 * DefaultLoanInterestCalculator - Concrete implementation for unknown/default loan types
 * Fixed rate: 10%
 * Used when loan type is null or not recognized
 * Extends AbstractLoanInterestCalculator to leverage the Template Method pattern
 */
public class DefaultLoanInterestCalculator extends AbstractLoanInterestCalculator {

    private static final double RATE = 10.0;

    @Override
    protected double getRate(double amount, int durationMonths) {
        // Default loan: fixed 10% rate for unknown/unspecified loan types
        return RATE;
    }

    @Override
    public String getLoanType() {
        return "DEFAULT";
    }
}
