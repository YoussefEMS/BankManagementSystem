package com.bms.payment;

import java.math.BigDecimal;

/**
 * Immutable value object representing a payment request in the BMS domain.
 * Gateway adapters translate this into their vendor-specific format.
 */
public class PaymentRequest {
    private final String transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final String customerEmail;

    public PaymentRequest(String transactionId, BigDecimal amount, String currency,
                          String description, String customerEmail) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.customerEmail = customerEmail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
}
