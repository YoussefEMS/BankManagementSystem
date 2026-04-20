package com.bms.strategy.interest;

public class BusinessInterestStrategy implements InterestCalculationStrategy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.03;
    }
}