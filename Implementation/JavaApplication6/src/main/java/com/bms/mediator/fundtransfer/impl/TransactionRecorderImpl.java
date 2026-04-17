package com.bms.mediator.fundtransfer.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.bms.mediator.fundtransfer.FundTransferContext;
import com.bms.mediator.fundtransfer.peers.ITransactionRecorderPeer;

public class TransactionRecorderImpl implements ITransactionRecorderPeer {
    private static final Logger logger = Logger.getLogger(TransactionRecorderImpl.class.getName());
    
    private static final AtomicInteger transactionIdCounter = new AtomicInteger(50000);
    private final Map<Integer, TransactionRecord> transactionLog;
    private final Map<String, Integer> linkedTransactions;

    public TransactionRecorderImpl() {
        this.transactionLog = new HashMap<>();
        this.linkedTransactions = new HashMap<>();
    }

    @Override
    public int recordDebitTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context) {
        try {
            int txnId = transactionIdCounter.incrementAndGet();
            TransactionRecord record = new TransactionRecord(
                    txnId, accountNo, amount, "DEBIT", referenceCode, LocalDateTime.now(), "COMPLETED");

            transactionLog.put(txnId, record);
            logger.info("Debit transaction recorded: ID=" + txnId + 
                       ", Account=" + accountNo + 
                       ", Amount=" + amount);
            return txnId;
        } catch (Exception e) {
            logger.warning("Failed to record debit transaction: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int recordCreditTransaction(String accountNo, double amount, String referenceCode, FundTransferContext context) {
        try {
            int txnId = transactionIdCounter.incrementAndGet();
            TransactionRecord record = new TransactionRecord(
                    txnId, accountNo, amount, "CREDIT", referenceCode, LocalDateTime.now(), "COMPLETED");

            transactionLog.put(txnId, record);
            logger.info("Credit transaction recorded: ID=" + txnId + 
                       ", Account=" + accountNo + 
                       ", Amount=" + amount);
            return txnId;
        } catch (Exception e) {
            logger.warning("Failed to record credit transaction: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean linkTransactionPair(int debitTransactionId, int creditTransactionId) {
        try {
            String linkKey = debitTransactionId + "-" + creditTransactionId;
            linkedTransactions.put(linkKey, debitTransactionId);
            
            logger.info("Transactions linked: Debit=" + debitTransactionId + 
                       ", Credit=" + creditTransactionId);
            return true;
        } catch (Exception e) {
            logger.warning("Failed to link transaction pair: " + e.getMessage());
            return false;
        }
    }

    private static class TransactionRecord {
        private final int transactionId;
        private final String accountNo;
        private final double amount;
        private final String type; // DEBIT or CREDIT
        private final String referenceCode;
        private final LocalDateTime timestamp;
        private final String status;

        TransactionRecord(int id, String account, double amt, String txnType, String refCode, LocalDateTime ts, String stat) {
            this.transactionId = id;
            this.accountNo = account;
            this.amount = amt;
            this.type = txnType;
            this.referenceCode = refCode;
            this.timestamp = ts;
            this.status = stat;
        }

        public int getTransactionId() { return transactionId; }
        public String getAccountNo() { return accountNo; }
        public double getAmount() { return amount; }
        public String getType() { return type; }
        public String getReferenceCode() { return referenceCode; }
    }
}
