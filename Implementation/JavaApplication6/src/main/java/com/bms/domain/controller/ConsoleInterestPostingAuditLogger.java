package com.bms.domain.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bms.domain.entity.MonthlyInterestContext;
import com.bms.domain.controller.InterestPostingAuditLogger;

public class ConsoleInterestPostingAuditLogger implements InterestPostingAuditLogger {
    private static final Logger logger = Logger.getLogger(ConsoleInterestPostingAuditLogger.class.getName());
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private StringBuilder auditLog;

    public ConsoleInterestPostingAuditLogger() {
        this.auditLog = new StringBuilder();
        initializeLogger();
    }

    private void initializeLogger() {
        try {
            FileHandler fh = new FileHandler("monthly_interest_audit.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.warning("Could not initialize file handler: " + e.getMessage());
        }
    }

    @Override
    public void logBatchStart(MonthlyInterestContext context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] BATCH_START - Run Date: %s", timestamp, context.getRunDate());
        auditLog.append(message).append("\n");
        logger.info(message);
    }

    @Override
    public void logSuccessfulInterestPosting(String accountNo, double interestAmount, double newBalance, MonthlyInterestContext context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] SUCCESS - Account: %s, Interest: %.2f, New Balance: %.2f", 
                timestamp, accountNo, interestAmount, newBalance);
        auditLog.append(message).append("\n");
        logger.info(message);
    }

    @Override
    public void logFailedInterestPosting(String accountNo, String reason, MonthlyInterestContext context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] FAILURE - Account: %s, Reason: %s", 
                timestamp, accountNo, reason);
        auditLog.append(message).append("\n");
        logger.warning(message);
    }

    @Override
    public void logBatchCompletion(MonthlyInterestContext context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] BATCH_COMPLETE - Total Accounts: %d, Successful: %d, Failed: %d, Total Interest: %.2f",
                timestamp, 
                context.getTotalAccountsProcessed(),
                context.getSuccessfulOperations(),
                context.getFailedOperations(),
                context.getTotalInterestPosted());
        auditLog.append(message).append("\n");
        logger.info(message);
    }

    @Override
    public void logComplianceEvent(String event, MonthlyInterestContext context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] COMPLIANCE - %s", timestamp, event);
        auditLog.append(message).append("\n");
        logger.warning(message);
    }

    public String getAuditLog() {
        return auditLog.toString();
    }
}
