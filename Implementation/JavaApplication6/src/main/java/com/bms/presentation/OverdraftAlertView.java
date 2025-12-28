package com.bms.presentation;

import java.util.Date;

/**
 * 
 */
public class OverdraftAlertView {

    /**
     * Default constructor
     */
    public OverdraftAlertView() {
    }

    /**
     * 
     */
    private String displayMessage;

    /**
     * 
     */
    private String displayAccountNo;

    /**
     * 
     */
    private double displayNegativeBalance;

    /**
     * 
     */
    private Date displayTimestamp;




    /**
     * @param accountNo 
     * @param newBalance 
     * @param timestamp 
     * @return
     */
    public void showOverdraftAlert(String accountNo, double newBalance, Date timestamp) {
        // TODO implement here
    }

}