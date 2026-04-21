package com.bms.domain.entity;

import java.util.Date;

public class MonthlyInterestContext {
    private Date runDate;
    private int totalAccountsProcessed;
    private double totalInterestPosted;
    private int successfulOperations;
    private int failedOperations;
    private StringBuilder executionLog;

    public MonthlyInterestContext(Date runDate) {
        this.runDate = runDate;
        this.totalAccountsProcessed = 0;
        this.totalInterestPosted = 0.0;
        this.successfulOperations = 0;
        this.failedOperations = 0;
        this.executionLog = new StringBuilder();
    }

    // Getters and Setters
    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public int getTotalAccountsProcessed() {
        return totalAccountsProcessed;
    }

    public void incrementAccountsProcessed() {
        this.totalAccountsProcessed++;
    }

    public double getTotalInterestPosted() {
        return totalInterestPosted;
    }

    public void addToTotalInterest(double amount) {
        this.totalInterestPosted += amount;
    }

    public int getSuccessfulOperations() {
        return successfulOperations;
    }

    public void incrementSuccessfulOperations() {
        this.successfulOperations++;
    }

    public int getFailedOperations() {
        return failedOperations;
    }

    public void incrementFailedOperations() {
        this.failedOperations++;
    }

    public StringBuilder getExecutionLog() {
        return executionLog;
    }

    public void logMessage(String message) {
        this.executionLog.append(message).append("\n");
    }

    @Override
    public String toString() {
        return "MonthlyInterestContext{" +
                "runDate=" + runDate +
                ", totalAccountsProcessed=" + totalAccountsProcessed +
                ", totalInterestPosted=" + totalInterestPosted +
                ", successfulOperations=" + successfulOperations +
                ", failedOperations=" + failedOperations +
                '}';
    }
}
