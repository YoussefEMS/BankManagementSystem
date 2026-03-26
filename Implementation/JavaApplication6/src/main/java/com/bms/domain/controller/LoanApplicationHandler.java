package com.bms.domain.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.DAOFactory;

/**
 * LoanApplicationHandler - UC-09: Apply for Loan
 * Validates loan inputs, creates a PENDING loan application
 */
public class LoanApplicationHandler {
    private final Map<String, AbstractLoanApplicationProcessor> processors;
    private final AbstractLoanApplicationProcessor defaultProcessor;

    public LoanApplicationHandler() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public LoanApplicationHandler(DAOFactory factory) {
        this.processors = new HashMap<>();
        this.processors.put("PERSONAL", new PersonalLoanApplicationProcessor(factory));
        this.processors.put("HOME", new HomeLoanApplicationProcessor(factory));
        this.processors.put("AUTO", new AutoLoanApplicationProcessor(factory));
        this.defaultProcessor = new DefaultLoanApplicationProcessor(factory);
    }

    /**
     * Submit a loan application
     * 
     * @return the generated loan ID (> 0 on success), negative on failure
     */
    public int applyForLoan(int customerId, double amount, String loanType,
            int durationMonths, String purpose) {
        if (loanType == null || loanType.trim().isEmpty()) {
            return -1;
        }

        return getProcessor(loanType).applyForLoan(customerId, amount, loanType, durationMonths, purpose);
    }

    private AbstractLoanApplicationProcessor getProcessor(String loanType) {
        return processors.getOrDefault(loanType.trim().toUpperCase(Locale.ROOT), defaultProcessor);
    }
}
