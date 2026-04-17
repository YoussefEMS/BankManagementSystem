package com.bms.mediator.monthlyinterest.peers;
import com.bms.mediator.monthlyinterest.MonthlyInterestContext;

public interface INotificationServicePeer {
    boolean notifyInterestPosting(int customerId, String accountNo, double interestAmount, double newBalance, MonthlyInterestContext context);
    boolean notifyBatchCompletion(MonthlyInterestContext context);
    boolean notifyInterestPostingFailure(int customerId, String accountNo, String reason, MonthlyInterestContext context);
}
