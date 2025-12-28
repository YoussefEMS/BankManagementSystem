package com.bms.domain.entity;

import java.util.Date;

/**
 * 
 */
public class OverdraftEvent {

    /**
     * Default constructor
     */
    public OverdraftEvent() {
    }

    /**
     * 
     */
    private int overdraftID;

    /**
     * 
     */
    private String accountNumber;

    /**
     * 
     */
    private Date timestamp;

    /**
     * 
     */
    private double amount;

    /**
     * 
     */
    private boolean alertSent;

    /**
     * 
     */
    private int transactionID;



    /**
     * @param accountNumber 
     * @param transactionID 
     * @param timestamp 
     * @param amount 
     * @param alertSent 
     * @return
     */
    public void create(String accountNumber, int transactionID, Date timestamp, double amount, boolean alertSent) {
        // TODO implement here
    }

    /**
     * @return
     */
    public int getOverDraftID() {
        // TODO implement here
        return 0;
    }

    /**
     * @return
     */
    public String getAccountNumber() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public Date getTimeStamp() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public double getAmount() {
        // TODO implement here
        return 0;
    }

    /**
     * @return
     */
    public int getTransactionID() {
        // TODO implement here
        return 0;
    }

}