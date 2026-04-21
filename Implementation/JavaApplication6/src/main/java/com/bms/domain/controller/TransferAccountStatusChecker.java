package com.bms.domain.controller;
import com.bms.domain.entity.FundTransferContext;


public interface TransferAccountStatusChecker {
    boolean validateAccountsStatus(String sourceAccountNo, String destinationAccountNo, FundTransferContext context);
    String getAccountStatus(String accountNo);
    boolean hasTransferRestrictions(String accountNo);
}
