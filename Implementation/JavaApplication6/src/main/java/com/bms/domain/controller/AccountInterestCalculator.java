package com.bms.domain.controller;

public class AccountInterestCalculator {

    private InterestRatePolicy policy;

    public AccountInterestCalculator(InterestRatePolicy policy) {
        this.policy = policy;
    }

    public void setPolicy(InterestRatePolicy policy) {
        this.policy = policy;
    }

    public double calculate(double balance) {
        return policy.calculateInterest(balance);
    }
}
