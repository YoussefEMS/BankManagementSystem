package com.bms.domain.entity;

import java.time.LocalDateTime;

/**
 * InterestPosting Entity - represents interest posted to an account
 * Domain layer entity - no dependencies on DAO or data layer
 */
public class InterestPosting {
    private int postingId;
    private String accountNumber;
    private double amount;
    private double rateUsed;
    private LocalDateTime timestamp;

    public InterestPosting() {
    }

    // Getters and Setters
    public int getPostingId() {
        return postingId;
    }

    public void setPostingId(int postingId) {
        this.postingId = postingId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRateUsed() {
        return rateUsed;
    }

    public void setRateUsed(double rateUsed) {
        this.rateUsed = rateUsed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "InterestPosting{" +
                "postingId=" + postingId +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", rateUsed=" + rateUsed +
                '}';
    }
}