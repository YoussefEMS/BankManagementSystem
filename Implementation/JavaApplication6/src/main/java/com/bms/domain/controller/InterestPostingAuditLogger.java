package com.bms.domain.controller;
import com.bms.domain.entity.MonthlyInterestContext;

public interface InterestPostingAuditLogger {
    void logBatchStart(MonthlyInterestContext context);
    void logSuccessfulInterestPosting(String accountNo, double interestAmount, double newBalance, MonthlyInterestContext context);
    void logFailedInterestPosting(String accountNo, String reason, MonthlyInterestContext context);
    void logBatchCompletion(MonthlyInterestContext context);
    void logComplianceEvent(String event, MonthlyInterestContext context);
}
