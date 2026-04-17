package com.bms.mediator.monthlyinterest.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.bms.mediator.monthlyinterest.MonthlyInterestContext;
import com.bms.mediator.monthlyinterest.peers.ITransactionFactoryPeer;

public class TransactionFactoryPeerImpl implements ITransactionFactoryPeer {
    private static final Logger logger = Logger.getLogger(TransactionFactoryPeerImpl.class.getName());
    
    private static final AtomicInteger transactionIdCounter = new AtomicInteger(10000);
    private static final double MINIMUM_TRANSACTION_AMOUNT = 0.01;

    @Override
    public int createInterestTransaction(String accountNo, double interestAmount, double currentBalance, 
                                         double newBalance, MonthlyInterestContext context) {
        if (!validateTransactionData(accountNo, interestAmount)) {
            logger.warning("Invalid transaction data");
            return -1;
        }

        int transactionId = transactionIdCounter.incrementAndGet();
        
        logger.info("Created transaction ID: " + transactionId + 
                   " for account: " + accountNo + 
                   " amount: " + interestAmount);

        return transactionId;
    }

    @Override
    public boolean validateTransactionData(String accountNo, double interestAmount) {
        // Validate account number format
        if (accountNo == null || accountNo.trim().isEmpty()) {
            logger.warning("Invalid account number");
            return false;
        }

        // Validate interest amount is positive and significant
        if (interestAmount < MINIMUM_TRANSACTION_AMOUNT) {
            logger.warning("Interest amount too small: " + interestAmount);
            // Still valid - zero interest transactions are acceptable
            return true;
        }

        // Validate reasonable upper bound
        if (interestAmount > 1000000.0) {
            logger.warning("Interest amount unreasonably high: " + interestAmount);
            return false;
        }

        return true;
    }
}
