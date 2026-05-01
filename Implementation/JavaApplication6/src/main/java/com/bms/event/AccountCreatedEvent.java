package com.bms.event;

public class AccountCreatedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final int customerId;
    private final String accountType;

    public AccountCreatedEvent(String accountNumber, int customerId, String accountType) {
        super(accountNumber);
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getAccountType() {
        return accountType;
    }
}
