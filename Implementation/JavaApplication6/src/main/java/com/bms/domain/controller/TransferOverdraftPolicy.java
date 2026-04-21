package com.bms.domain.controller;

import com.bms.domain.entity.FundTransferContext;
public interface TransferOverdraftPolicy {
    boolean isOverdraftAllowed(String accountNo);
    boolean isWithinOverdraftLimit(String accountNo, double transferAmount, double currentBalance, FundTransferContext context);
    double getOverdraftLimit(String accountNo);
    boolean willTriggerOverdraft(String accountNo, double newBalance);
}
