package com.bms.domain.controller;

import com.bms.domain.entity.FundTransferContext;
public interface TransferTransactionRecorder {
    int recordDebitTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context);
    int recordCreditTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context);
    boolean linkTransactionPair(int debitTransactionId, int creditTransactionId);
}
