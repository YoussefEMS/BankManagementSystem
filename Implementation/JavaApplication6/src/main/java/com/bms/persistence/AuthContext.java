package com.bms.persistence;

import com.bms.domain.entity.Customer;

/**
 * AuthContext - Singleton to manage the currently logged-in customer session
 * Provides session state throughout the application
 */
public class AuthContext {
    private static volatile AuthContext instance;
    private Customer loggedInCustomer;

    private AuthContext() {
        // Private constructor for singleton
    }

    /**
     * Get the singleton instance
     */
    public static AuthContext getInstance() {
        if (instance == null) {
            synchronized (AuthContext.class) {
                if (instance == null) {
                    instance = new AuthContext();
                }
            }
        }
        return instance;
    }

    /**
     * Centralized login operation.
     * Returns false when a session is already active or input is invalid.
     */
    public synchronized boolean login(Customer customer) {
        if (customer == null || loggedInCustomer != null) {
            return false;
        }
        this.loggedInCustomer = customer;
        return true;
    }

    /**
     * Get the logged-in customer
     */
    public synchronized Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    /**
     * Get the logged-in customer ID
     */
    public synchronized int getLoggedInCustomerId() {
        return loggedInCustomer != null ? loggedInCustomer.getCustomerId() : -1;
    }

    /**
     * Check if a customer is logged in
     */
    public synchronized boolean isLoggedIn() {
        return loggedInCustomer != null;
    }

    /**
     * Check if the logged-in user has the ADMIN role
     * 
     * @return true if user is logged in and has ADMIN role
     */
    public synchronized boolean isAdmin() {
        return loggedInCustomer != null && "ADMIN".equals(loggedInCustomer.getRole());
    }

    /**
     * Get the role of the logged-in user
     * 
     * @return role string if logged in, null otherwise
     */
    public synchronized String getRole() {
        return loggedInCustomer != null ? loggedInCustomer.getRole() : null;
    }

    /**
     * Clear the session (logout)
     */
    public synchronized void logout() {
        this.loggedInCustomer = null;
    }
}
