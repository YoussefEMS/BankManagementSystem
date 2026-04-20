package com.bms.domain.controller;

/**
 * AutoLoanInterestCalculator - Concrete implementation for auto loans
 * Fixed rate: 8%
 * Auto loans are secured by vehicle, so lower risk than personal loans
 * Extends AbstractLoanInterestCalculator to leverage the Template Method pattern
 */
public class AutoLoanInterestCalculator extends AbstractLoanInterestCalculator {

    private static final double RATE = 8.0;

    @Override
    protected double getRate(double amount, int durationMonths) {
        // Auto loan: fixed 8% rate regardless of amount or duration
        // In future, could add logic for rate variations based on vehicle type, age, etc.
        return RATE;
    }

    @Override
    public String getLoanType() {
        return "AUTO";
    }
}
