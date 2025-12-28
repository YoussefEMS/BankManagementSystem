package com.bms.domain.entity;

import java.util.Date;

/**
 * 
 */
public class Loan {

    /**
     * Default constructor
     */
    public Loan() {
    }

    /**
     * 
     */
    private int loanId;

    /**
     * 
     */
    private double amount;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private Date submissionDate;

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

    /**
     * @return
     */
    public int getLoanID() {
        // TODO implement here
        return 0;
    }

    /**
     * @param newStatus 
     * @return
     */
    public void updateStatus(String newStatus) {
        // TODO implement here
    }

}