package com.bms.domain.controller;

import com.bms.domain.entity.MonthlyInterestContext;
public interface InterestTransactionBuilder {

    int createInterestTransaction(String accountNo, double interestAmount, double currentBalance, 
                                   double newBalance, MonthlyInterestContext context);
    boolean validateTransactionData(String accountNo, double interestAmount);
}
