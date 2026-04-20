package com.bms.strategy.interest;

public class StandardInterestStrategy implements InterestCalculationStrategy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.02;
    }
}