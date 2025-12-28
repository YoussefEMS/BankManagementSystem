package com.bms.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Entity - represents a bank transaction
 * Domain layer entity - no dependencies on DAO or data layer
 */
public class Transaction {
    private int transactionId;
    private String accountNumber;
    private String type;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String performedBy;
    private String note;
    private BigDecimal balanceAfter;
    private String referenceCode;

    // Constructors
    public Transaction() {
    }

    public Transaction(int transactionId, String accountNumber, String type, 
                      BigDecimal amount, LocalDateTime timestamp, String performedBy,
                      String note, BigDecimal balanceAfter, String referenceCode) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.performedBy = performedBy;
        this.note = note;
        this.balanceAfter = balanceAfter;
        this.referenceCode = referenceCode;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", note='" + note + '\'' +
                ", balanceAfter=" + balanceAfter +
                '}';
    }
}
