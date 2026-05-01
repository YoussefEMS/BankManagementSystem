package com.bms.event;

public class CustomerAuthenticatedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final int customerId;
    private final String email;
    private final String role;

    public CustomerAuthenticatedEvent(int customerId, String email, String role) {
        super(String.valueOf(customerId));
        this.customerId = customerId;
        this.email = email;
        this.role = role;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
