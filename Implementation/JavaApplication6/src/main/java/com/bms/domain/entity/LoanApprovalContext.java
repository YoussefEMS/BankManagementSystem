package com.bms.domain.entity;

import java.math.BigDecimal;

import com.bms.domain.controller.LoanApprovalPolicy;

public class LoanApprovalContext {
    private LoanApprovalPolicy policy;

    public LoanApprovalContext(LoanApprovalPolicy policy) {
        this.policy = policy;
    }

    public void setPolicy(LoanApprovalPolicy policy) {
        this.policy = policy;
    }

    public boolean approveLoan(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (policy == null) {
            throw new IllegalStateException("Loan approval strategy is not set.");
        }
        return policy.approve(loanAmount, customerBalance);
    }
}
