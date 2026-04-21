package com.bms.domain.controller;

import java.util.List;

import com.bms.domain.entity.MonthlyInterestContext;

public interface MonthlyInterestAccounts {
    List<String> getActiveAccounts(MonthlyInterestContext context);
    String getAccountType(String accountNo);
    double getAccountBalance(String accountNo);
}
