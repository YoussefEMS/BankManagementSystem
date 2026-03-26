package com.bms.domain.controller;

import com.bms.domain.entity.Loan;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.LoanDAO;

/**
 * Bridge abstraction for submitting loan applications.
 * The application workflow is decoupled from the interest calculation algorithm.
 */
public abstract class AbstractLoanApplicationProcessor {
    protected final LoanInterestCalculator calculator;
    protected final LoanDAO loanDAO;

    protected AbstractLoanApplicationProcessor(LoanInterestCalculator calculator, DAOFactory factory) {
        this.calculator = calculator;
        this.loanDAO = factory.createLoanDAO();
    }

    public int applyForLoan(int customerId, double amount, String requestedLoanType,
            int durationMonths, String purpose) {
        if (!validateInputs(amount, durationMonths, purpose)) {
            return -1;
        }

        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setAmount(amount);
        loan.setLoanType(resolveLoanType(requestedLoanType));
        loan.setDurationMonths(durationMonths);
        loan.setPurpose(purpose);
        loan.setStatus("PENDING");
        loan.setInterestRate(calculator.calculateRate(amount, durationMonths));

        return loanDAO.insert(loan);
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
