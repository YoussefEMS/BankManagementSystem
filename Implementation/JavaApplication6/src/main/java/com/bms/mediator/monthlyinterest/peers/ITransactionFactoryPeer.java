package com.bms.mediator.monthlyinterest.peers;

import com.bms.mediator.monthlyinterest.MonthlyInterestContext;
public interface ITransactionFactoryPeer {

    int createInterestTransaction(String accountNo, double interestAmount, double currentBalance, 
                                   double newBalance, MonthlyInterestContext context);
    boolean validateTransactionData(String accountNo, double interestAmount);
}
