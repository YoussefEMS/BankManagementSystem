package com.bms.domain.controller;

import java.util.Set;

import com.bms.domain.entity.Loan;

/**
 * 
 */
public class LoanApplicationHandler {

    /**
     * Default constructor
     */
    public LoanApplicationHandler() {
    }





    /**
     * @param customerId 
     * @param amount 
     * @param loanType 
     * @param durationMonths 
     * @param purpose 
     * @return
     */
    public Set<Loan> applyForLoan(int customerId, double amount, String loanType, int durationMonths, String purpose) {
        // TODO implement here
        return null;
    }

    /**
     * @param amount 
     * @param loanType 
     * @param durationMonths 
     * @param purpose 
     * @return
     */
    private boolean validateInputs(double amount, String loanType, int durationMonths, String purpose) {
        // TODO implement here
        return false;
    }

    /**
     * @return
     */
    private java.util.Date now() {
        // TODO implement here
        return null;
    }

}