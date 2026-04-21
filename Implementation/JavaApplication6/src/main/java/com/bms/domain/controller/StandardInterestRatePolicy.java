package com.bms.domain.controller;

public class StandardInterestRatePolicy implements InterestRatePolicy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.02;
    }
}