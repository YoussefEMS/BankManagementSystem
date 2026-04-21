package com.bms.domain.controller;

import java.math.BigDecimal;

public class StandardLoanApprovalPolicy implements LoanApprovalPolicy {

    @Override
    public boolean approve(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (loanAmount == null || customerBalance == null) {
            return false;
        }

        // stricter rule: loan must not exceed 2x customer balance
        return loanAmount.compareTo(customerBalance.multiply(BigDecimal.valueOf(2))) <= 0;
    }
}