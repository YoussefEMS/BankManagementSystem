package com.bms.event;

import java.time.LocalDate;

public class InterestPostedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final LocalDate runDate;
    private final int accountCount;

    public InterestPostedEvent(LocalDate runDate, int accountCount) {
        super(runDate != null ? runDate.toString() : "");
        this.runDate = runDate;
        this.accountCount = accountCount;
    }

    public LocalDate getRunDate() {
        return runDate;
    }

    public int getAccountCount() {
        return accountCount;
    }
}
