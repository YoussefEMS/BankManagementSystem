package com.bms.domain.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.bms.domain.entity.MonthlyInterestContext;
import com.bms.domain.controller.InterestNotificationSender;

public class ConsoleInterestNotificationSender implements InterestNotificationSender {
    private static final Logger logger = Logger.getLogger(ConsoleInterestNotificationSender.class.getName());
    
    private final List<String> sentNotifications;

    public ConsoleInterestNotificationSender() {
        this.sentNotifications = new ArrayList<>();
    }

    @Override
    public boolean notifyInterestPosting(int customerId, String accountNo, double interestAmount, 
                                        double newBalance, MonthlyInterestContext context) {
        try {
            String notification = String.format(
                    "Customer %d - Interest Posted to Account %s: Amount = %.2f, New Balance = %.2f",
                    customerId, accountNo, interestAmount, newBalance);
            
            sentNotifications.add(notification);
            logger.info("Notification sent: " + notification);
            
            // In real implementation, would send email/SMS
            return true;
        } catch (Exception e) {
            logger.warning("Failed to send notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notifyBatchCompletion(MonthlyInterestContext context) {
        try {
            String notification = String.format(
                    "BATCH COMPLETION REPORT - Accounts Processed: %d, Successful: %d, Failed: %d, " +
                    "Total Interest Posted: %.2f",
                    context.getTotalAccountsProcessed(),
                    context.getSuccessfulOperations(),
                    context.getFailedOperations(),
                    context.getTotalInterestPosted());
            
            sentNotifications.add(notification);
            logger.info("Batch completion notification sent: " + notification);
            
            // In real implementation, would send to admin/operations team
            return true;
        } catch (Exception e) {
            logger.warning("Failed to send batch notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notifyInterestPostingFailure(int customerId, String accountNo, String reason, 
                                               MonthlyInterestContext context) {
        try {
            String notification = String.format(
                    "ALERT - Customer %d: Interest posting failed for Account %s. Reason: %s",
                    customerId, accountNo, reason);
            
            sentNotifications.add(notification);
            logger.warning("Failure notification sent: " + notification);
            
            // In real implementation, would send alert notification
            return true;
        } catch (Exception e) {
            logger.warning("Failed to send failure notification: " + e.getMessage());
            return false;
        }
    }

    public List<String> getSentNotifications() {
        return new ArrayList<>(sentNotifications);
    }
}
