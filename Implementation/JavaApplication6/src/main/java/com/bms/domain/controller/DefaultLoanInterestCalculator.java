package com.bms.domain.controller;

/**
 * DefaultLoanInterestCalculator - Concrete implementation for unknown/default loan types
 * Fixed rate: 10%
 * Used when loan type is null or not recognized
 * Part of the Bridge design pattern implementation
 */
public class DefaultLoanInterestCalculator implements LoanInterestCalculator {

    private static final double RATE = 10.0;

    @Override
    public double calculateRate(double amount, int durationMonths) {
        // Default loan: fixed 10% rate for unknown/unspecified loan types
        return RATE;
    }

    @Override
    public String getLoanType() {
        return "DEFAULT";
    }
}
