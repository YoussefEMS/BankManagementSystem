package com.bms.strategy.interest;

public class PremiumInterestStrategy implements InterestCalculationStrategy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.04;
    }
}