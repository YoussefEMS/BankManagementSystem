package com.bms.mediator.monthlyinterest.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bms.mediator.monthlyinterest.IMonthlyInterestMediator;
import com.bms.mediator.monthlyinterest.MonthlyInterestContext;
import com.bms.mediator.monthlyinterest.peers.IAccountPeer;
import com.bms.mediator.monthlyinterest.peers.IAccountUpdaterPeer;
import com.bms.mediator.monthlyinterest.peers.IAuditLoggerPeer;
import com.bms.mediator.monthlyinterest.peers.IInterestCalculatorPeer;
import com.bms.mediator.monthlyinterest.peers.INotificationServicePeer;
import com.bms.mediator.monthlyinterest.peers.ITransactionFactoryPeer;

public class MonthlyInterestMediatorImpl implements IMonthlyInterestMediator {
    private static final Logger logger = Logger.getLogger(MonthlyInterestMediatorImpl.class.getName());

    // Peer references - dependencies are inverted (interfaces, not concrete classes)
    private IAccountPeer accountPeer;
    private IInterestCalculatorPeer interestCalculatorPeer;
    private ITransactionFactoryPeer transactionFactoryPeer;
    private IAccountUpdaterPeer accountUpdaterPeer;
    private IAuditLoggerPeer auditLoggerPeer;
    private INotificationServicePeer notificationServicePeer;


    private final Map<String, Object> peerRegistry = new HashMap<>();

    public MonthlyInterestMediatorImpl(
            IAccountPeer accountPeer,
            IInterestCalculatorPeer interestCalculatorPeer,
            ITransactionFactoryPeer transactionFactoryPeer,
            IAccountUpdaterPeer accountUpdaterPeer,
            IAuditLoggerPeer auditLoggerPeer,
            INotificationServicePeer notificationServicePeer) {
        this.accountPeer = accountPeer;
        this.interestCalculatorPeer = interestCalculatorPeer;
        this.transactionFactoryPeer = transactionFactoryPeer;
        this.accountUpdaterPeer = accountUpdaterPeer;
        this.auditLoggerPeer = auditLoggerPeer;
        this.notificationServicePeer = notificationServicePeer;
    }

    /**
     * Main orchestration method that coordinates the monthly interest posting workflow
     * 
     * Workflow:
     * 1. Initialize context and begin audit logging
     * 2. Fetch all active accounts
     * 3. For each account:
     *    a. Check eligibility
     *    b. Calculate interest
     *    c. Create transaction
     *    d. Update balance
     *    e. Notify customer
     * 4. Complete batch and notify stakeholders
     */
    @Override
    public MonthlyInterestContext postMonthlyInterest(java.util.Date runDate) {
        MonthlyInterestContext context = new MonthlyInterestContext(runDate);
        
        try {
            logger.log(Level.INFO, "Starting monthly interest batch processing for date: {0}", runDate);
            
            // Step 1: Initialize - Begin audit logging
            auditLoggerPeer.logBatchStart(context);

            // Step 2: Fetch active accounts (Mediator coordinates with Account Peer)
            List<String> activeAccounts = accountPeer.getActiveAccounts(context);

            if (activeAccounts == null) {
                throw new IllegalArgumentException("Active accounts list cannot be null");
            }

            if (activeAccounts.isEmpty()) {
                auditLoggerPeer.logBatchCompletion(context);
                return context;
            }

            // Step 3: Process each account (Orchestration begins here)
            for (String accountNo : activeAccounts) {
                processAccountInterest(accountNo, context);
            }

            // Step 4: Finalize - Complete batch operations
            context.logMessage("Batch processing completed");
            auditLoggerPeer.logBatchCompletion(context);
            notificationServicePeer.notifyBatchCompletion(context);

            logger.log(Level.INFO, "Monthly interest batch completed successfully. " +
                    "Processed: {0}, Successful: {1}, Failed: {2}",
                    new Object[]{context.getTotalAccountsProcessed(), 
                               context.getSuccessfulOperations(), 
                               context.getFailedOperations()});

        } catch (IllegalArgumentException e) {
            // Re-throw validation errors so callers can handle them
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during monthly interest batch processing", e);
            context.logMessage("BATCH FAILED: " + e.getMessage());
            auditLoggerPeer.logComplianceEvent("Batch processing failed: " + e.getMessage(), context);
        }

        return context;
    }

   
    private void processAccountInterest(String accountNo, MonthlyInterestContext context) {
        try {
            context.incrementAccountsProcessed();
            
            // Step 1: Get account information (Mediator -> Account Peer)
            String accountType = accountPeer.getAccountType(accountNo);
            double currentBalance = accountPeer.getAccountBalance(accountNo);

            // Step 2: Check eligibility (Mediator -> Interest Calculator Peer)
            if (!interestCalculatorPeer.isEligibleForInterest(accountType, null)) {
                return;  // Not eligible, not counted as failure
            }

            // Step 3: Get interest rate (Mediator -> Interest Calculator Peer)
            double interestRate = interestCalculatorPeer.getMonthlyInterestRate(accountType, null);

            // Step 4: Calculate interest (Mediator -> Interest Calculator Peer)
            double interestAmount = interestCalculatorPeer.calculateInterest(
                    currentBalance, interestRate, accountType, null);

            // Step 5: Validate transaction (Mediator -> Transaction Factory Peer)
            if (!transactionFactoryPeer.validateTransactionData(accountNo, interestAmount)) {
                context.incrementFailedOperations();
                auditLoggerPeer.logFailedInterestPosting(accountNo, "Transaction validation failed", null);
                return;
            }

            // Step 6: Create transaction record (Mediator -> Transaction Factory Peer)
            int transactionId = transactionFactoryPeer.createInterestTransaction(
                    accountNo, interestAmount, currentBalance, 
                    currentBalance + interestAmount, null);

            // Check if transaction creation succeeded
            if (transactionId <= 0) {
                context.incrementFailedOperations();
                auditLoggerPeer.logFailedInterestPosting(accountNo, "Transaction creation failed", null);
                return;
            }

            // Step 7: Update account balance (Mediator -> Account Updater Peer)
            double newBalance = currentBalance + interestAmount;
            boolean updateSuccess = accountUpdaterPeer.updateAccountBalance(
                    accountNo, newBalance, interestAmount, null);

            if (!updateSuccess) {
                context.incrementFailedOperations();
                auditLoggerPeer.logFailedInterestPosting(accountNo, "Balance update failed", null);
                return;
            }

            // Step 8: Record metadata (Mediator -> Account Updater Peer)
            accountUpdaterPeer.recordInterestPostingMetadata(accountNo, transactionId, null);

            // Step 9: Log success (Mediator -> Audit Logger Peer)
            auditLoggerPeer.logSuccessfulInterestPosting(accountNo, interestAmount, newBalance, null);
            context.incrementSuccessfulOperations();
            context.addToTotalInterest(interestAmount);

            // Step 10: Notify customer (Mediator -> Notification Service Peer)
            int customerId = 1; // Default customer ID (would be fetched from account in real system)
            notificationServicePeer.notifyInterestPosting(customerId, accountNo, interestAmount, newBalance, null);

        } catch (Exception e) {
            context.incrementFailedOperations();
            logger.log(Level.WARNING, "Exception processing account " + accountNo, e);
            try {
                auditLoggerPeer.logFailedInterestPosting(accountNo, e.getMessage(), null);
            } catch (Exception inner) {
                logger.log(Level.WARNING, "Failed to log audit record", inner);
            }
        }
    }

    private int extractCustomerIdFromAccount(String accountNo) {
        // Simplified: extract from account format or query database
        // For demo purposes, returning a dummy value
        return accountNo.hashCode() % 1000;
    }

    @Override
    public void registerPeer(String peerName, Object peer) {
        peerRegistry.put(peerName, peer);
        logger.log(Level.INFO, "Peer registered: {0}", peerName);
    }

    @Override
    public void unregisterPeer(String peerName) {
        peerRegistry.remove(peerName);
        logger.log(Level.INFO, "Peer unregistered: {0}", peerName);
    }
}
