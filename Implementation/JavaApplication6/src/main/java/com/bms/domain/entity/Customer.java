package com.bms.domain.entity;

/**
 * Customer Entity - represents a bank customer
 * Domain layer entity - no dependencies on DAO or data layer
 */
public class Customer {
    private int customerID;
    private String fullName;
    private String nationalID;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String customerTier;
    private String role;
    private String status;

    /**
     * Default constructor
     */
    public Customer() {
    }

    /**
     * Constructor with all fields
     */
    public Customer(int customerID, String fullName, String nationalID, String email,
            String password, String address, String phoneNumber, String customerTier,
            String role, String status) {
        this.customerID = customerID;
        this.fullName = fullName;
        this.nationalID = nationalID;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
        this.role = role != null ? role : "CUSTOMER";
        this.status = status;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerID;
    }

    public void setCustomerId(int customerID) {
        this.customerID = customerID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTier() {
        return customerTier;
    }

    public void setTier(String tier) {
        this.customerTier = tier;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", tier='" + customerTier + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}