package com.bms.event;

public class CustomerProfileCreatedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int customerId;
    private final String fullName;
    private final String email;

    public CustomerProfileCreatedEvent(int customerId, String fullName, String email) {
        super(String.valueOf(customerId));
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}
