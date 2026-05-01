package com.bms.event;

import java.math.BigDecimal;

public class FundTransferCompletedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int customerId;
    private final String sourceAccountNumber;
    private final String destinationAccountNumber;
    private final BigDecimal amount;
    private final String referenceCode;

    public FundTransferCompletedEvent(int customerId, String sourceAccountNumber,
            String destinationAccountNumber, BigDecimal amount, String referenceCode) {
        super(referenceCode);
        this.customerId = customerId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.referenceCode = referenceCode;
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

    public String getReferenceCode() {
        return referenceCode;
    }
}
