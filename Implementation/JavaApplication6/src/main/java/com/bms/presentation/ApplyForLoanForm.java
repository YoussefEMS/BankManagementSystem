package com.bms.presentation;

import java.util.Set;

import com.bms.domain.entity.Loan;

/**
 * 
 */
public class ApplyForLoanForm {

    /**
     * Default constructor
     */
    public ApplyForLoanForm() {
    }

    /**
     * 
     */
    private double loanAmount;

    /**
     * 
     */
    private String loanType;

    /**
     * 
     */
    private int durationMonths;

    /**
     * 
     */
    private String purpose;

    /**
     * 
     */
    private String statusMessage;


    /**
     * @param amount 
     * @param loanType 
     * @param durationMonths 
     * @param purpose 
     * @return
     */
    public void enterLoanData(double amount, String loanType, int durationMonths, String purpose) {

    }

    /**
     * @return
     */
    public void submitApplication() {
        // TODO implement here
    }

    /**
     * @param loanId 
     * @param loans 
     * @return
     */
    public void displayConfirmation(int loanId, Set<Loan> loans) {
        // TODO implement here
    }

}