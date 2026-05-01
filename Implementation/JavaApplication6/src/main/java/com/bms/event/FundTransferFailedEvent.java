package com.bms.event;

import java.math.BigDecimal;

public class FundTransferFailedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int customerId;
    private final String sourceAccountNumber;
    private final String destinationAccountNumber;
    private final BigDecimal amount;
    private final String reason;

    public FundTransferFailedEvent(int customerId, String sourceAccountNumber,
            String destinationAccountNumber, BigDecimal amount, String reason) {
        super(sourceAccountNumber);
        this.customerId = customerId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.reason = reason;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }
}
