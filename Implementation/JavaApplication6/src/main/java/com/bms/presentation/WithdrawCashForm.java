package com.bms.presentation;

/**
 * 
 */
public class WithdrawCashForm {

    /**
     * Default constructor
     */
    public WithdrawCashForm() {
    }

    /**
     * 
     */
    private String selectedAccountNo;

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
    private String displayedStatus;

    /**
     * 
     */
    private double displayedUpdatedBalance;


    /**
     * @param accountNo 
     * @return
     */
    public void selectAccount(String accountNo) {
        // TODO implement here
    }

    /**
     * @param amount 
     * @param description 
     * @return
     */
    public void enterWithdrawal(double amount, String description) {
        // TODO implement here
    }

    /**
     * @return
     */
    public void submitWithdrawal() {
        // TODO implement here
    }

    /**
     * @param updatedBalance 
     * @param transactionId 
     * @return
     */
    public void displaySuccess(double updatedBalance, int transactionId) {
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