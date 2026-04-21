package com.bms.strategy.loan;

import java.math.BigDecimal;

public interface LoanApprovalStrategy {
    boolean approve(BigDecimal loanAmount, BigDecimal customerBalance);
}