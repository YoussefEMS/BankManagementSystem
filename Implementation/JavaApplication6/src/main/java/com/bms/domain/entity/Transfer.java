package com.bms.domain.entity;

import java.time.LocalDateTime;

/**
 * Transfer Entity - represents a fund transfer between accounts
 * Domain layer entity - no dependencies on DAO or data layer
 */
public class Transfer {
    private int transferId;
    private String fromAccountNo;
    private String toAccountNo;
    private double amount;
    private LocalDateTime timestamp;
    private String referenceCode;
    private String status;

    public Transfer() {
    }

    // Getters and Setters
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getFromAccountNo() {
        return fromAccountNo;
    }

    public void setFromAccountNo(String fromAccountNo) {
        this.fromAccountNo = fromAccountNo;
    }

    public String getToAccountNo() {
        return toAccountNo;
    }

    public void setToAccountNo(String toAccountNo) {
        this.toAccountNo = toAccountNo;
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

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", from='" + fromAccountNo + '\'' +
                ", to='" + toAccountNo + '\'' +
                ", amount=" + amount +
                ", referenceCode='" + referenceCode + '\'' +
                '}';
    }
}