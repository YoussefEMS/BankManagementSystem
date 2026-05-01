package com.bms.event;

public class AccountStatusChangedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final String previousStatus;
    private final String newStatus;
    private final int adminId;

    public AccountStatusChangedEvent(String accountNumber, String previousStatus, String newStatus, int adminId) {
        super(accountNumber);
        this.accountNumber = accountNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.adminId = adminId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public int getAdminId() {
        return adminId;
    }
}
