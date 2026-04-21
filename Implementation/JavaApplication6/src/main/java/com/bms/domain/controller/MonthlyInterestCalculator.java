package com.bms.domain.controller;

import com.bms.domain.entity.MonthlyInterestContext;
public interface MonthlyInterestCalculator {
    double getMonthlyInterestRate(String accountType, MonthlyInterestContext context);
    double calculateInterest(double currentBalance, double interestRate, String accountType, MonthlyInterestContext context);
    boolean isEligibleForInterest(String accountType, MonthlyInterestContext context);
}
