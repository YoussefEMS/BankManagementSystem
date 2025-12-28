package com.bms.presentation;

/**
 * 
 */
public class DepositCashForm {

    /**
     * Default constructor
     */
    public DepositCashForm() {
    }

    /**
     * 
     */
    private String accountNo;

    /**
     * 
     */
    private double amount;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private double displayBalance;


    /**
     * @param accountNo 
     * @param amount 
     * @param description 
     * @return
     */
    public void enterDeposit(String accountNo, double amount, String description) {
        // TODO implement here
    }

    /**
     * @return
     */
    public void submitDeposit() {
        // TODO implement here
    }

    /**
     * @param newBalance 
     * @param transactionId 
     * @return
     */
    public void depositPosted(double newBalance, int transactionId) {
        // TODO implement here
    }

    /**
     * @param message 
     * @return
     */
    public void displayError(String message) {
        // TODO implement here
    }

}