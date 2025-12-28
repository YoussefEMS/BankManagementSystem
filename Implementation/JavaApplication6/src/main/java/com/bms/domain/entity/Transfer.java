package com.bms.domain.entity;

import java.util.Date;

/**
 * 
 */
public class Transfer {

    /**
     * Default constructor
     */
    public Transfer() {
    }

    /**
     * 
     */
    private int transferId;

    /**
     * 
     */
    private String fromAccountNo;

    /**
     * 
     */
    private String toAccountNo;

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
    private String referenceCode;

    /**
     * 
     */
    private String status;


    /**
     * @param fromAccountNo 
     * @param toAccountNo 
     * @param amount 
     * @param timestamp 
     * @param referenceCode 
     * @return
     */
    public void createTransfer(String fromAccountNo, String toAccountNo, double amount, Date timestamp, String referenceCode) {
        // TODO implement here
    }

    /**
     * @return
     */
    public int getTransferId() {
        // TODO implement here
        return 0;
    }

    /**
     * @return
     */
    public String getfromAccountNo() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public String gettoAccountNo() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public String getReferenceCode() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public String getStatus() {
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

}