package com.bms.domain.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;

/**
 * LoanApplicationService - UC-09: Apply for Loan
 * Routes the request to the correct loan application processor.
 */
public class LoanApplicationService {
    private final Map<String, AbstractLoanApplicationProcessor> processors;
    private final AbstractLoanApplicationProcessor defaultProcessor;

    public static class LoanResult {
        private final int loanId;
        private final String status;

        public LoanResult(int loanId, String status) {
            this.loanId = loanId;
            this.status = status;
        }

        public int getLoanId() {
            return loanId;
        }

        public String getStatus() {
            return status;
        }
    }

    public LoanApplicationService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public LoanApplicationService(PersistenceProvider factory) {
        this.processors = new HashMap<>();
        this.processors.put("PERSONAL", new PersonalLoanApplicationProcessor(factory));
        this.processors.put("HOME", new HomeLoanApplicationProcessor(factory));
        this.processors.put("AUTO", new AutoLoanApplicationProcessor(factory));
        this.defaultProcessor = new DefaultLoanApplicationProcessor(factory);
    }

    /**
     * Submit a loan application and return both loan ID and status.
     */
    public LoanResult applyForLoan(int customerId, double amount, String loanType,
            int durationMonths, String purpose) {
        if (loanType == null || loanType.trim().isEmpty()) {
            return new LoanResult(-1, null);
        }

        AbstractLoanApplicationProcessor.ProcessingResult result =
                getProcessor(loanType).applyForLoanWithResult(customerId, amount, loanType, durationMonths, purpose);

        if (result == null) {
            return new LoanResult(-1, null);
        }

        return new LoanResult(result.getLoanId(), result.getStatus());
    }

    private AbstractLoanApplicationProcessor getProcessor(String loanType) {
        return processors.getOrDefault(loanType.trim().toUpperCase(Locale.ROOT), defaultProcessor);
    }
}