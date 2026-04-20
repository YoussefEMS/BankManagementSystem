package com.bms.strategy.interest;

public class InterestService {

    public double calculateInterest(String accountType, double balance) {

        InterestCalculationStrategy strategy;

        if (accountType.equalsIgnoreCase("STANDARD")) {
            strategy = new StandardInterestStrategy();
        } else if (accountType.equalsIgnoreCase("PREMIUM")) {
            strategy = new PremiumInterestStrategy();
        } else if (accountType.equalsIgnoreCase("BUSINESS")) {
            strategy = new BusinessInterestStrategy();
        } else {
            throw new IllegalArgumentException("Unknown type");
        }

        InterestCalculator calculator = new InterestCalculator(strategy);
        return calculator.calculate(balance);
    }
}