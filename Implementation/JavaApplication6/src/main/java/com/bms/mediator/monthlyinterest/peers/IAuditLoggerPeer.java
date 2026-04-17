package com.bms.mediator.monthlyinterest.peers;
import com.bms.mediator.monthlyinterest.MonthlyInterestContext;

public interface IAuditLoggerPeer {
    void logBatchStart(MonthlyInterestContext context);
    void logSuccessfulInterestPosting(String accountNo, double interestAmount, double newBalance, MonthlyInterestContext context);
    void logFailedInterestPosting(String accountNo, String reason, MonthlyInterestContext context);
    void logBatchCompletion(MonthlyInterestContext context);
    void logComplianceEvent(String event, MonthlyInterestContext context);
}
