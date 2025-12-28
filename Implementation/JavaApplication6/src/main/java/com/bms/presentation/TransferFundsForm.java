package com.bms.presentation;

/**
 * 
 */
public class TransferFundsForm {

    /**
     * Default constructor
     */
    public TransferFundsForm() {
    }

    /**
     * 
     */
    private String sourceAccountNo;

    /**
     * 
     */
    private String destinationAccountNo;

    /**
     * 
     */
    private double amount;

    /**
     * 
     */
    private double displaySourceBalance;

    /**
     * 
     */
    private double displayDestinationBalance;

    /**
     * 
     */
    private String displayReferenceCode;


    /**
     * @param sourceAccountNo 
     * @param destinationAccountNo 
     * @param amount 
     * @return
     */
    public void enterTransfer(String sourceAccountNo, String destinationAccountNo, double amount) {
        // TODO implement here
    }

    /**
     * @return
     */
    public void submitTransfer() {
        // TODO implement here
    }

    /**
     * @param referenceCode 
     * @param newSrcBalance 
     * @param newDstBalance 
     * @return
     */
    public void displayTransferResult(String referenceCode, double newSrcBalance, double newDstBalance) {
        // TODO implement here
    }

}