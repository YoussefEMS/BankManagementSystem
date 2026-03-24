package com.bms.domain.controller;

/**
 * HomeLoanInterestCalculator - Concrete implementation for home loans
 * Fixed rate: 5%
 * Home loans are secured by property, so lower interest rate
 * Part of the Bridge design pattern implementation
 */
public class HomeLoanInterestCalculator implements LoanInterestCalculator {

    private static final double RATE = 5.0;

    @Override
    public double calculateRate(double amount, int durationMonths) {
        // Home loan: fixed 5% rate regardless of amount or duration
        // In future, could add logic for rate variations based on property value, location, etc.
        return RATE;
    }

    @Override
    public String getLoanType() {
        return "HOME";
    }
}
