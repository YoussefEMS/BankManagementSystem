package com.bms.domain.controller;
import com.bms.domain.entity.MonthlyInterestContext;

public interface InterestNotificationSender {
    boolean notifyInterestPosting(int customerId, String accountNo, double interestAmount, double newBalance, MonthlyInterestContext context);
    boolean notifyBatchCompletion(MonthlyInterestContext context);
    boolean notifyInterestPostingFailure(int customerId, String accountNo, String reason, MonthlyInterestContext context);
}
