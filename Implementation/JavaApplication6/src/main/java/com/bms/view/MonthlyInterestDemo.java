package com.bms.view;

import java.util.Date;

import com.bms.domain.controller.MonthlyInterestWorkflow;
import com.bms.domain.entity.MonthlyInterestContext;
import com.bms.domain.controller.DatabaseMonthlyInterestAccounts;
import com.bms.domain.controller.AccountInterestBalanceUpdater;
import com.bms.domain.controller.ConsoleInterestPostingAuditLogger;
import com.bms.domain.controller.AccountTypeMonthlyInterestCalculator;
import com.bms.domain.controller.ConsoleInterestNotificationSender;
import com.bms.domain.controller.InterestPostingTransactionBuilder;

public class MonthlyInterestDemo {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("MONTHLY INTEREST MEDIATOR PATTERN DEMO");
        System.out.println("========================================\n");

        // Step 1: Create all peers
        System.out.println("Step 1: Initializing Peers");
        System.out.println("--------------------------");
        
        DatabaseMonthlyInterestAccounts accountPeer = new DatabaseMonthlyInterestAccounts();
        System.out.println("✓ Account Peer initialized");
        
        AccountTypeMonthlyInterestCalculator calculatorPeer = new AccountTypeMonthlyInterestCalculator();
        System.out.println("✓ Interest Calculator Peer initialized");
        
        InterestPostingTransactionBuilder transactionPeer = new InterestPostingTransactionBuilder();
        System.out.println("✓ Transaction Factory Peer initialized");
        
        AccountInterestBalanceUpdater updaterPeer = new AccountInterestBalanceUpdater();
        System.out.println("✓ Account Updater Peer initialized");
        
        ConsoleInterestPostingAuditLogger auditPeer = new ConsoleInterestPostingAuditLogger();
        System.out.println("✓ Audit Logger Peer initialized");
        
        ConsoleInterestNotificationSender notificationPeer = new ConsoleInterestNotificationSender();
        System.out.println("✓ Notification Service Peer initialized");

        // Step 2: Create mediator
        System.out.println("\nStep 2: Creating Mediator");
        System.out.println("------------------------");
        
        MonthlyInterestWorkflow mediator = new com.bms.domain.controller.MonthlyInterestWorkflowCoordinator(
                accountPeer,
                calculatorPeer,
                transactionPeer,
                updaterPeer,
                auditPeer,
                notificationPeer);
        System.out.println("✓ Monthly Interest Mediator created");

        // Step 3: Execute batch processing
        System.out.println("\nStep 3: Executing Monthly Interest Batch");
        System.out.println("---------------------------------------");
        
        MonthlyInterestContext result = mediator.postMonthlyInterest(new Date());

        // Step 4: Display results
        System.out.println("\nStep 4: Batch Results");
        System.out.println("--------------------");
        System.out.println("Total Accounts Processed: " + result.getTotalAccountsProcessed());
        System.out.println("Successful Operations: " + result.getSuccessfulOperations());
        System.out.println("Failed Operations: " + result.getFailedOperations());
        System.out.println("Total Interest Posted: $" + String.format("%.2f", result.getTotalInterestPosted()));

        // Step 5: Display execution log
        System.out.println("\nStep 5: Execution Log");
        System.out.println("-------------------");
        System.out.println(result.getExecutionLog().toString());

        // Step 6: Display notifications sent
        System.out.println("\nStep 6: Customer Notifications");
        System.out.println("-----------------------------");
        for (String notification : notificationPeer.getSentNotifications()) {
            System.out.println("→ " + notification);
        }

        System.out.println("\n========================================");
        System.out.println("DEMO COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }
}
