package com.bms.mediator.monthlyinterest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.mediator.monthlyinterest.MonthlyInterestContext;
import com.bms.mediator.monthlyinterest.peers.IAccountUpdaterPeer;

public class AccountUpdaterPeerImpl implements IAccountUpdaterPeer {
    private static final Logger logger = Logger.getLogger(AccountUpdaterPeerImpl.class.getName());
    
    // Mock account balances - in real system, would update database
    private final Map<String, Double> accountBalances;
    private final Map<String, Integer> lastInterestTransactionIds;

    public AccountUpdaterPeerImpl() {
        this.accountBalances = new HashMap<>();
        this.lastInterestTransactionIds = new HashMap<>();
        // Initialize with some mock data
        initializeMockBalances();
    }

    private void initializeMockBalances() {
        accountBalances.put("ACC001", 10000.0);
        accountBalances.put("ACC002", 50000.0);
        accountBalances.put("ACC003", 100000.0);
        accountBalances.put("ACC004", 5000.0);
        accountBalances.put("ACC005", 250000.0);
    }

    @Override
    public boolean updateAccountBalance(String accountNo, double newBalance, double interestAmount, MonthlyInterestContext context) {
        try {
            // Validate inputs
            if (accountNo == null || accountNo.trim().isEmpty()) {
                logger.warning("Invalid account number");
                return false;
            }

            if (newBalance < 0) {
                logger.warning("Attempted to set negative balance: " + newBalance);
                return false;
            }

            // Update balance
            accountBalances.put(accountNo, newBalance);
            
            logger.info("Updated account balance for " + accountNo + 
                       ": New balance = " + newBalance + 
                       ", Interest = " + interestAmount);
            
            return true;
        } catch (Exception e) {
            logger.warning("Error updating account balance: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean recordInterestPostingMetadata(String accountNo, int transactionId, MonthlyInterestContext context) {
        try {
            if (transactionId <= 0) {
                logger.warning("Invalid transaction ID: " + transactionId);
                return false;
            }

            // Record the transaction ID for audit purposes
            lastInterestTransactionIds.put(accountNo, transactionId);
            
            logger.info("Recorded interest posting metadata for account " + accountNo + 
                       ": Transaction ID = " + transactionId);
            
            return true;
        } catch (Exception e) {
            logger.warning("Error recording metadata: " + e.getMessage());
            return false;
        }
    }

    public double getAccountBalance(String accountNo) {
        return accountBalances.getOrDefault(accountNo, 0.0);
    }
}
