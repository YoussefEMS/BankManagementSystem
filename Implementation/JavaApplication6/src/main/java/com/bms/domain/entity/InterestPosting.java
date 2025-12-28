package com.bms.domain.entity;

import java.util.Date;

/**
 * 
 */
public class InterestPosting {

    /**
     * Default constructor
     */
    public InterestPosting() {
    }

    /**
     * 
     */
    private int postingID;

    /**
     * 
     */
    private String accountNumber;

    /**
     * 
     */
    private double amount;

    /**
     * 
     */
    private Date timestamp;

    /**
     * 
     */
    private double rateUsed;

    /**
     * @param accountNumber 
     * @param amount 
     * @param timestamp 
     * @param rateUsed 
     * @return
     */
    public void createSuccess(String accountNumber, double amount, Date timestamp, double rateUsed) {
        // TODO implement here
    }

}