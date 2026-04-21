package com.bms.domain.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.Loan;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.LoanDAO;
import com.bms.persistence.ConfiguredPersistenceProvider;

/**
 * LoanDecisionService - UC-10: Approve / Reject Loans
 * Allows admins to review pending loans and make decisions
 */
public class LoanDecisionService {
    private final LoanDAO loanDAO;

    public LoanDecisionService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public LoanDecisionService(PersistenceProvider factory) {
        this.loanDAO = factory.createLoanDAO();
    }

    /**
     * Get all pending loan applications
     */
    public List<Loan> getPendingLoans() {
        return loanDAO.findByStatus("PENDING");
    }

    /**
     * Get details for a specific loan
     */
    public Loan getLoanDetails(int loanId) {
        return loanDAO.findById(loanId);
    }

    /**
     * Approve or reject a loan
     * 
     * @param loanId   the loan ID
     * @param decision "APPROVED" or "REJECTED"
     * @return true on success
     */
    public boolean decideLoan(int loanId, String decision) {
        // Validate decision
        if (decision == null || (!decision.equals("APPROVED") && !decision.equals("REJECTED"))) {
            return false;
        }

        // Verify loan exists and is PENDING
        Loan loan = loanDAO.findById(loanId);
        if (loan == null || !"PENDING".equals(loan.getStatus())) {
            return false;
        }

        // Update decision
        return loanDAO.updateDecision(loanId, decision, LocalDateTime.now());
    }
}
