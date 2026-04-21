package com.bms.strategy.loan;

import java.math.BigDecimal;

public class StandardLoanApprovalStrategy implements LoanApprovalStrategy {

    @Override
    public boolean approve(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (loanAmount == null || customerBalance == null) {
            return false;
        }

        // stricter rule: loan must not exceed 2x customer balance
        return loanAmount.compareTo(customerBalance.multiply(BigDecimal.valueOf(2))) <= 0;
    }
}