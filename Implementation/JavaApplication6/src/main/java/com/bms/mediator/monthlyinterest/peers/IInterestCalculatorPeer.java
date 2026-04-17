package com.bms.mediator.monthlyinterest.peers;

import com.bms.mediator.monthlyinterest.MonthlyInterestContext;
public interface IInterestCalculatorPeer {
    double getMonthlyInterestRate(String accountType, MonthlyInterestContext context);
    double calculateInterest(double currentBalance, double interestRate, String accountType, MonthlyInterestContext context);
    boolean isEligibleForInterest(String accountType, MonthlyInterestContext context);
}
