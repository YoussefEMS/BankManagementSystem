package com.bms.event;

public class AccountClosedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final int adminId;

    public AccountClosedEvent(String accountNumber, int adminId) {
        super(accountNumber);
        this.accountNumber = accountNumber;
        this.adminId = adminId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getAdminId() {
        return adminId;
    }
}
