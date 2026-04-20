package com.bms.domain.strategy.loan;

import java.math.BigDecimal;

public class LoanApprovalContext {
    private LoanApprovalStrategy strategy;

    public LoanApprovalContext(LoanApprovalStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(LoanApprovalStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean approveLoan(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (strategy == null) {
            throw new IllegalStateException("Loan approval strategy is not set.");
        }
        return strategy.approve(loanAmount, customerBalance);
    }
}