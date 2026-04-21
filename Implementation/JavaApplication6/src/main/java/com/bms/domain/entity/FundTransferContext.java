package com.bms.domain.entity;

public class FundTransferContext {
    private String sourceAccountNo;
    private String destinationAccountNo;
    private int customerId;
    private double transferAmount;
    private String transferReference;
    private boolean transferSuccessful;
    private String failureReason;
    private double sourceNewBalance;
    private double destinationNewBalance;
    private int transactionId;

    public FundTransferContext(String sourceAccountNo, String destinationAccountNo, 
                              int customerId, double transferAmount) {
        this.sourceAccountNo = sourceAccountNo;
        this.destinationAccountNo = destinationAccountNo;
        this.customerId = customerId;
        this.transferAmount = transferAmount;
        this.transferSuccessful = false;
    }

    // Getters and Setters
    public String getSourceAccountNo() { return sourceAccountNo; }
    public String getDestinationAccountNo() { return destinationAccountNo; }
    public int getCustomerId() { return customerId; }
    public double getTransferAmount() { return transferAmount; }
    public String getTransferReference() { return transferReference; }
    public void setTransferReference(String transferReference) { this.transferReference = transferReference; }
    public boolean isTransferSuccessful() { return transferSuccessful; }
    public void setTransferSuccessful(boolean successful) { this.transferSuccessful = successful; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String reason) { this.failureReason = reason; }
    public double getSourceNewBalance() { return sourceNewBalance; }
    public void setSourceNewBalance(double balance) { this.sourceNewBalance = balance; }
    public double getDestinationNewBalance() { return destinationNewBalance; }
    public void setDestinationNewBalance(double balance) { this.destinationNewBalance = balance; }
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int id) { this.transactionId = id; }

    @Override
    public String toString() {
        return "FundTransferContext{" +
                "source=" + sourceAccountNo +
                ", destination=" + destinationAccountNo +
                ", amount=" + transferAmount +
                ", successful=" + transferSuccessful +
                ", failure=" + failureReason +
                '}';
    }
}
