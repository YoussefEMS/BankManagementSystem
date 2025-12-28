package com.bms.domain.controller;

import com.bms.domain.entity.Customer;

/**
 * 
 */
public class CustomerProfileController {

    /**
     * Default constructor
     */
    public CustomerProfileController() {
    }




    /**
     * @param fullName 
     * @param email 
     * @param mobilePhone 
     * @param address 
     * @param nationalID 
     * @return
     */
    public int createCustomerProfile(String fullName, String email, String mobilePhone, String address, String nationalID) {
        // TODO implement here
        return 0;
    }

    /**
     * @param fullName 
     * @param emailAddress 
     * @param mobilePhone 
     * @return
     */
    private boolean validateInput(String fullName, String emailAddress, String mobilePhone) {
        // TODO implement here
        return false;
    }

    /**
     * @param emailAddress 
     * @return
     */
    private boolean isEmailUnique(String emailAddress) {
        // TODO implement here
        return false;
    }

    /**
     * @param fullName 
     * @param email 
     * @param mobilePhone 
     * @param address 
     * @param nationalID 
     * @return
     */
    private Customer buildCustomer(String fullName, String email, String mobilePhone, String address, String nationalID) {
        // TODO implement here
        return null;
    }

}