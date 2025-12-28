package com.bms.persistence;

import com.bms.domain.entity.Customer;

/**
 * AuthContext - Singleton to manage the currently logged-in customer session
 * Provides session state throughout the application
 */
public class AuthContext {
    private static AuthContext instance;
    private Customer loggedInCustomer;

    private AuthContext() {
        // Private constructor for singleton
    }

    /**
     * Get the singleton instance
     */
    public static AuthContext getInstance() {
        if (instance == null) {
            instance = new AuthContext();
        }
        return instance;
    }

    /**
     * Set the logged-in customer
     */
    public void setLoggedInCustomer(Customer customer) {
        this.loggedInCustomer = customer;
    }

    /**
     * Get the logged-in customer
     */
    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    /**
     * Get the logged-in customer ID
     */
    public int getLoggedInCustomerId() {
        return loggedInCustomer != null ? loggedInCustomer.getCustomerId() : -1;
    }

    /**
     * Check if a customer is logged in
     */
    public boolean isLoggedIn() {
        return loggedInCustomer != null;
    }

    /**
     * Clear the session (logout)
     */
    public void logout() {
        this.loggedInCustomer = null;
    }
}
