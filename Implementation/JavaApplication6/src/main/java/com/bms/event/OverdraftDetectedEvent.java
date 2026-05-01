package com.bms.event;

import java.math.BigDecimal;

public class OverdraftDetectedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final BigDecimal balance;

    public OverdraftDetectedEvent(String accountNumber, BigDecimal balance) {
        super(accountNumber);
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
