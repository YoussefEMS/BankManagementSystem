package com.bms.domain.controller;
import com.bms.domain.entity.MonthlyInterestContext;

public interface InterestBalanceUpdater {

    boolean updateAccountBalance(String accountNo, double newBalance, double interestAmount, MonthlyInterestContext context);
    boolean recordInterestPostingMetadata(String accountNo, int transactionId, MonthlyInterestContext context);
}
