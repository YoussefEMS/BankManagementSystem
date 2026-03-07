package com.bms.domain.entity;

import java.time.LocalDateTime;

/**
 * OverdraftEvent Entity - represents an overdraft event on an account
 * Domain layer entity - no dependencies on DAO or data layer
 */
public class OverdraftEvent {
    private int overdraftId;
    private String accountNumber;
    private int transactionId;
    private double amount;
    private LocalDateTime timestamp;
    private boolean alertSent;

    public OverdraftEvent() {
    }

    // Getters and Setters
    public int getOverdraftId() {
        return overdraftId;
    }

    public void setOverdraftId(int overdraftId) {
        this.overdraftId = overdraftId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAlertSent() {
        return alertSent;
    }

    public void setAlertSent(boolean alertSent) {
        this.alertSent = alertSent;
    }

    @Override
    public String toString() {
        return "OverdraftEvent{" +
                "overdraftId=" + overdraftId +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", alertSent=" + alertSent +
                '}';
    }
}