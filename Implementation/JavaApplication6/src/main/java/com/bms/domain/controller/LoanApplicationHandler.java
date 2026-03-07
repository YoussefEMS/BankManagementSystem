package com.bms.domain.controller;

import com.bms.domain.entity.Loan;
import com.bms.persistence.LoanDAO;

/**
 * LoanApplicationHandler - UC-09: Apply for Loan
 * Validates loan inputs, creates a PENDING loan application
 */
public class LoanApplicationHandler {
    private final LoanDAO loanDAO;

    public LoanApplicationHandler() {
        this.loanDAO = new LoanDAO();
    }

    /**
     * Submit a loan application
     * 
     * @return the generated loan ID (> 0 on success), negative on failure
     */
    public int applyForLoan(int customerId, double amount, String loanType,
            int durationMonths, String purpose) {
        // Validate inputs
        if (!validateInputs(amount, loanType, durationMonths, purpose)) {
            return -1;
        }

        // Build loan entity
        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setAmount(amount);
        loan.setLoanType(loanType);
        loan.setDurationMonths(durationMonths);
        loan.setPurpose(purpose);
        loan.setStatus("PENDING");

        // Set a default interest rate based on loan type
        double interestRate = getDefaultRate(loanType);
        loan.setInterestRate(interestRate);

        // Persist
        return loanDAO.insert(loan);
    }

    private boolean validateInputs(double amount, String loanType, int durationMonths, String purpose) {
        if (amount <= 0)
            return false;
        if (loanType == null || loanType.trim().isEmpty())
            return false;
        if (durationMonths <= 0 || durationMonths > 360)
            return false;
        if (purpose == null || purpose.trim().isEmpty())
            return false;
        return true;
    }

    private double getDefaultRate(String loanType) {
        if (loanType == null)
            return 10.0;
        return switch (loanType.toUpperCase()) {
            case "PERSONAL" -> 15.0;
            case "HOME" -> 5.0;
            case "AUTO" -> 8.0;
            default -> 10.0;
        };
    }
}