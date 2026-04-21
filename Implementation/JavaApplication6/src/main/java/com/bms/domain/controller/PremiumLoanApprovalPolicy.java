package com.bms.domain.controller;

import java.math.BigDecimal;

public class PremiumLoanApprovalPolicy implements LoanApprovalPolicy {

    @Override
    public boolean approve(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (loanAmount == null || customerBalance == null) {
            return false;
        }

        // easier rule: loan must not exceed 5x customer balance
        return loanAmount.compareTo(customerBalance.multiply(BigDecimal.valueOf(5))) <= 0;
    }
}