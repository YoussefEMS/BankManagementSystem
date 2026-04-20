package com.bms.strategy.interest;

public class InterestCalculator {

    private InterestCalculationStrategy strategy;

    public InterestCalculator(InterestCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(InterestCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(double balance) {
        return strategy.calculateInterest(balance);
    }
}