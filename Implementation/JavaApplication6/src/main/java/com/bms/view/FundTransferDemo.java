package com.bms.view;

import com.bms.domain.controller.FundTransferWorkflow;
import com.bms.domain.entity.FundTransferContext;
import com.bms.domain.controller.*;


public class FundTransferDemo {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("FUND TRANSFER MEDIATOR PATTERN DEMO");
        System.out.println("========================================\n");

        // Initialize peers
        System.out.println("Step 1: Initializing Peers");
        System.out.println("--------------------------");
        
        SourceAccountAvailabilityValidator sourceValidator = new SourceAccountAvailabilityValidator();
        System.out.println("✓ Source Account Validator initialized");
        
        DestinationAccountAvailabilityValidator destValidator = new DestinationAccountAvailabilityValidator();
        System.out.println("✓ Destination Account Validator initialized");
        
        ActiveTransferAccountStatusChecker statusHandler = new ActiveTransferAccountStatusChecker();
        System.out.println("✓ Account Status Handler initialized");
        
        AccountLimitOverdraftPolicy overdraftHandler = new AccountLimitOverdraftPolicy();
        System.out.println("✓ Overdraft Handler initialized");
        
        DatabaseTransferTransactionRecorder transactionRecorder = new DatabaseTransferTransactionRecorder();
        System.out.println("✓ Transaction Recorder initialized");
        
        AvailableFundsTransferValidator balanceValidator = new AvailableFundsTransferValidator();
        System.out.println("✓ Balance Validator initialized");

        // Create mediator
        System.out.println("\nStep 2: Creating Mediator");
        System.out.println("------------------------");
        
        FundTransferWorkflow mediator = new FundTransferWorkflowCoordinator(
                sourceValidator,
                destValidator,
                statusHandler,
                overdraftHandler,
                transactionRecorder,
                balanceValidator);
        System.out.println("✓ Fund Transfer Mediator created");

        // Test Case 1: Successful transfer
        System.out.println("\n========== TEST CASE 1: Successful Transfer ==========");
        System.out.println("From: ACC001, To: ACC002, Amount: $5,000");
        FundTransferContext result1 = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);
        displayTransferResult(result1);

        // Test Case 2: Insufficient funds transfer
        System.out.println("\n========== TEST CASE 2: Insufficient Funds ==========");
        System.out.println("From: ACC004, To: ACC005, Amount: $10,000");
        FundTransferContext result2 = mediator.transferFunds("ACC004", "ACC005", 1, 10000.0);
        displayTransferResult(result2);

        // Test Case 3: Invalid destination (inactive)
        System.out.println("\n========== TEST CASE 3: Inactive Destination ==========");
        System.out.println("From: ACC001, To: ACC003, Amount: $1,000");
        FundTransferContext result3 = mediator.transferFunds("ACC001", "ACC003", 1, 1000.0);
        displayTransferResult(result3);

        // Test Case 4: Overdraft scenario
        System.out.println("\n========== TEST CASE 4: With Overdraft ==========");
        System.out.println("From: ACC002, To: ACC001, Amount: $150,000 (triggers overdraft)");
        FundTransferContext result4 = mediator.transferFunds("ACC002", "ACC001", 2, 150000.0);
        displayTransferResult(result4);

        // Test Case 5: Invalid amount
        System.out.println("\n========== TEST CASE 5: Invalid Amount ==========");
        System.out.println("From: ACC001, To: ACC002, Amount: $0.001 (too small)");
        FundTransferContext result5 = mediator.transferFunds("ACC001", "ACC002", 1, 0.001);
        displayTransferResult(result5);

        System.out.println("\n========================================");
        System.out.println("DEMO COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }

    private static void displayTransferResult(FundTransferContext context) {
        System.out.println("\nResult:");
        System.out.println("-------");
        System.out.println("Status: " + (context.isTransferSuccessful() ? "✓ SUCCESS" : "✗ FAILED"));
        
        if (context.isTransferSuccessful()) {
            System.out.println("Reference: " + context.getTransferReference());
            System.out.println("Transaction ID: " + context.getTransactionId());
            System.out.println("Source New Balance: $" + String.format("%.2f", context.getSourceNewBalance()));
            System.out.println("Destination New Balance: $" + String.format("%.2f", context.getDestinationNewBalance()));
        } else {
            System.out.println("Reason: " + context.getFailureReason());
        }
    }
}
