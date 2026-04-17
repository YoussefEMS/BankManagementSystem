package com.bms.mediator.monthlyinterest.peers;
import com.bms.mediator.monthlyinterest.MonthlyInterestContext;

public interface IAccountUpdaterPeer {

    boolean updateAccountBalance(String accountNo, double newBalance, double interestAmount, MonthlyInterestContext context);
    boolean recordInterestPostingMetadata(String accountNo, int transactionId, MonthlyInterestContext context);
}
