package com.bms.domain.controller;

/**
 * PersonalLoanInterestCalculator - Concrete implementation for personal loans
 * Fixed rate: 15%
 * Personal loans have higher risk, so higher interest rate
 * Extends AbstractLoanInterestCalculator to leverage the Template Method pattern
 */
public class PersonalLoanInterestCalculator extends AbstractLoanInterestCalculator {

    private static final double RATE = 15.0;

    @Override
    protected double getRate(double amount, int durationMonths) {
        // Personal loan: fixed 15% rate regardless of amount or duration
        // In future, could add logic for tiered rates based on credit score, amount, etc.
        return RATE;
    }

    @Override
    public String getLoanType() {
        return "PERSONAL";
    }
}
