package com.bms.domain.controller;

import com.bms.domain.entity.Customer;
import com.bms.persistence.CustomerDAO;

/**
 * CustomerProfileController - UC-03: Create Customer Profile
 * Validates input, checks uniqueness, persists new customer
 */
public class CustomerProfileController {
    private final CustomerDAO customerDAO;

    public CustomerProfileController() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Create a new customer profile
     * 
     * @return the generated customer ID, or -1 if validation fails
     */
    public int createCustomerProfile(String fullName, String email, String mobilePhone,
            String address, String nationalID) {
        // Validate required fields
        if (!validateInput(fullName, email, mobilePhone)) {
            return -1;
        }

        // Check email uniqueness
        if (!isEmailUnique(email)) {
            return -2; // -2 indicates duplicate email
        }

        // Build and persist customer
        Customer customer = buildCustomer(fullName, email, mobilePhone, address, nationalID);
        return customerDAO.insert(customer);
    }

    private boolean validateInput(String fullName, String emailAddress, String mobilePhone) {
        if (fullName == null || fullName.trim().isEmpty())
            return false;
        if (emailAddress == null || emailAddress.trim().isEmpty())
            return false;
        if (mobilePhone == null || mobilePhone.trim().isEmpty())
            return false;
        // Basic email format check
        if (!emailAddress.contains("@"))
            return false;
        return true;
    }

    private boolean isEmailUnique(String emailAddress) {
        return !customerDAO.existsByEmail(emailAddress);
    }

    private Customer buildCustomer(String fullName, String email, String mobilePhone,
            String address, String nationalID) {
        Customer customer = new Customer();
        customer.setFullName(fullName.trim());
        customer.setEmail(email.trim());
        customer.setPhoneNumber(mobilePhone.trim());
        customer.setAddress(address != null ? address.trim() : "");
        customer.setNationalID(nationalID != null ? nationalID.trim() : "");
        customer.setPassword("changeme"); // Default password for admin-created customers
        customer.setRole("CUSTOMER");
        customer.setTier("SILVER"); // Default tier
        customer.setStatus("ACTIVE");
        return customer;
    }
}