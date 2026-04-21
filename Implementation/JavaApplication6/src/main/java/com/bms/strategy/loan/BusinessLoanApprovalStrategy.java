package com.bms.strategy.loan;

import java.math.BigDecimal;

public class BusinessLoanApprovalStrategy implements LoanApprovalStrategy {

    @Override
    public boolean approve(BigDecimal loanAmount, BigDecimal customerBalance) {
        if (loanAmount == null || customerBalance == null) {
            return false;
        }

        // business-specific rule: loan must not exceed 10x balance
        // and minimum balance should be at least 50,000
        return customerBalance.compareTo(BigDecimal.valueOf(50000)) >= 0
                && loanAmount.compareTo(customerBalance.multiply(BigDecimal.valueOf(10))) <= 0;
    }
}