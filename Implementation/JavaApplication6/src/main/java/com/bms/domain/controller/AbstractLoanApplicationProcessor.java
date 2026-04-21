package com.bms.domain.controller;

import java.math.BigDecimal;
import java.util.List;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Customer;
import com.bms.domain.entity.Loan;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.CustomerDAO;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.LoanDAO;
import com.bms.domain.entity.LoanApprovalContext;

/**
 * Bridge abstraction for submitting loan applications.
 * The application workflow is decoupled from the interest calculation algorithm.
 */
public abstract class AbstractLoanApplicationProcessor {
    protected final LoanInterestCalculator calculator;
    protected final LoanDAO loanDAO;
    protected final CustomerDAO customerDAO;
    protected final AccountDAO accountDAO;
    protected final LoanApprovalPolicySelector approvalPolicySelector;

    public static class ProcessingResult {
        private final int loanId;
        private final String status;

        public ProcessingResult(int loanId, String status) {
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

    protected AbstractLoanApplicationProcessor(LoanInterestCalculator calculator, PersistenceProvider factory) {
        this.calculator = calculator;
        this.loanDAO = factory.createLoanDAO();
        this.customerDAO = factory.createCustomerDAO();
        this.accountDAO = factory.createAccountDAO();
        this.approvalPolicySelector = new LoanApprovalPolicySelector();
    }

    /**
     * Keeps compatibility if any other class still expects only loan ID.
     */
    public int applyForLoan(int customerId, double amount, String requestedLoanType,
            int durationMonths, String purpose) {
        ProcessingResult result = applyForLoanWithResult(customerId, amount, requestedLoanType, durationMonths, purpose);
        return result != null ? result.getLoanId() : -1;
    }

    /**
     * Applies for a loan and returns both generated loan ID and final status.
     */
    public ProcessingResult applyForLoanWithResult(int customerId, double amount, String requestedLoanType,
            int durationMonths, String purpose) {
        if (!validateInputs(amount, durationMonths, purpose)) {
            return null;
        }

        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            return null;
        }

        List<Account> accounts = accountDAO.findByCustomerId(customerId);
        BigDecimal customerBalance = BigDecimal.ZERO;

        if (accounts != null) {
            for (Account account : accounts) {
                if (account.getBalance() != null) {
                    customerBalance = customerBalance.add(account.getBalance());
                }
            }
        }

        String customerTier = customer.getTier();
        LoanApprovalPolicy policy = approvalPolicySelector.getPolicy(customerTier);
        LoanApprovalContext context = new LoanApprovalContext(policy);

        boolean approved = context.approveLoan(BigDecimal.valueOf(amount), customerBalance);
        String finalStatus = approved ? "APPROVED" : "REJECTED";

        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setAmount(amount);
        loan.setLoanType(resolveLoanType(requestedLoanType));
        loan.setDurationMonths(durationMonths);
        loan.setPurpose(purpose);
        loan.setStatus(finalStatus);
        loan.setInterestRate(calculator.calculateRate(amount, durationMonths));

        int loanId = loanDAO.insert(loan);
        if (loanId <= 0) {
            return null;
        }

        return new ProcessingResult(loanId, finalStatus);
    }

    protected abstract String getLoanType();

    protected String resolveLoanType(String requestedLoanType) {
        return getLoanType();
    }

    protected boolean validateInputs(double amount, int durationMonths, String purpose) {
        if (amount <= 0) {
            return false;
        }
        if (durationMonths <= 0 || durationMonths > 360) {
            return false;
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
