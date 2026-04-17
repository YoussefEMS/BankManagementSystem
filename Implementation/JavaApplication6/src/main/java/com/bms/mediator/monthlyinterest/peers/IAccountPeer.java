package com.bms.mediator.monthlyinterest.peers;

import java.util.List;

import com.bms.mediator.monthlyinterest.MonthlyInterestContext;

public interface IAccountPeer {
    List<String> getActiveAccounts(MonthlyInterestContext context);
    String getAccountType(String accountNo);
    double getAccountBalance(String accountNo);
}
