package com.bms.domain.controller;

public class BusinessInterestRatePolicy implements InterestRatePolicy {

    @Override
    public double calculateInterest(double balance) {
        return balance * 0.03;
    }
}