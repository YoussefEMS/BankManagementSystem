package com.bms.event;

public class LoanRejectedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int loanId;
    private final int customerId;
    private final int adminId;

    public LoanRejectedEvent(int loanId, int customerId, int adminId) {
        super(String.valueOf(loanId));
        this.loanId = loanId;
        this.customerId = customerId;
        this.adminId = adminId;
    }

    public int getLoanId() {
        return loanId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAdminId() {
        return adminId;
    }
}
