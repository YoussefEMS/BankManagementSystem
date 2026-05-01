package com.bms.event;

import java.math.BigDecimal;

public class LoanApplicationSubmittedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int loanId;
    private final int customerId;
    private final BigDecimal amount;
    private final String loanType;
    private final int durationMonths;
    private final String status;

    public LoanApplicationSubmittedEvent(int loanId, int customerId, BigDecimal amount,
            String loanType, int durationMonths, String status) {
        super(String.valueOf(loanId));
        this.loanId = loanId;
        this.customerId = customerId;
        this.amount = amount;
        this.loanType = loanType;
        this.durationMonths = durationMonths;
        this.status = status;
    }

    public int getLoanId() {
        return loanId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getLoanType() {
        return loanType;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public String getStatus() {
        return status;
    }
}
