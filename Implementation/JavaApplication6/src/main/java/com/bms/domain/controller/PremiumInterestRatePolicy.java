package com.bms.domain.controller;

public class PremiumInterestRatePolicy implements InterestRatePolicy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.04;
    }
}