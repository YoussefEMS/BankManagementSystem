package com.bms.mediator.fundtransfer.peers;

import com.bms.mediator.fundtransfer.FundTransferContext;
public interface ITransactionRecorderPeer {
    int recordDebitTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context);
    int recordCreditTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context);
    boolean linkTransactionPair(int debitTransactionId, int creditTransactionId);
}
