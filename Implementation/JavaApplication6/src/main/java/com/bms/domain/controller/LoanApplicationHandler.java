package com.bms.domain.controller;

import com.bms.domain.entity.Loan;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.LoanDAO;
import com.bms.persistence.ConfiguredDAOFactory;

/**
 * LoanApplicationHandler - UC-09: Apply for Loan
 * Validates loan inputs, creates a PENDING loan application
 */
public class LoanApplicationHandler {
    private final LoanDAO loanDAO;

    public LoanApplicationHandler() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public LoanApplicationHandler(DAOFactory factory) {
        this.loanDAO = factory.createLoanDAO();
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

        // BRIDGE IN ACTION: Use the appropriate calculator strategy for this loan type
        LoanInterestCalculator calculator = getLoanCalculator(loanType);
        double interestRate = calculator.calculateRate(amount, durationMonths);
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

    private LoanInterestCalculator getLoanCalculator(String loanType) {
        if (loanType == null)
            return new DefaultLoanInterestCalculator(); // 10% default rate

        return switch (loanType.toUpperCase()) {
            case "PERSONAL" -> new PersonalLoanInterestCalculator();
            case "HOME" -> new HomeLoanInterestCalculator();
            case "AUTO" -> new AutoLoanInterestCalculator();
            default -> new DefaultLoanInterestCalculator(); // 10% default rate for unknown types
        };
    }
}
