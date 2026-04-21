package com.bms.domain.controller;

public class InterestService {

    public double calculateInterest(String accountType, double balance) {

        InterestRatePolicy policy;

        if (accountType.equalsIgnoreCase("STANDARD")) {
            policy = new StandardInterestRatePolicy();
        } else if (accountType.equalsIgnoreCase("PREMIUM")) {
            policy = new PremiumInterestRatePolicy();
        } else if (accountType.equalsIgnoreCase("BUSINESS")) {
            policy = new BusinessInterestRatePolicy();
        } else {
            throw new IllegalArgumentException("Unknown type");
        }

        AccountInterestCalculator calculator = new AccountInterestCalculator(policy);
        return calculator.calculate(balance);
    }
}
