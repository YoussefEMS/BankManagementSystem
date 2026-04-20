package com.bms.domain.strategy.loan;

import java.math.BigDecimal;

public interface LoanApprovalStrategy {
    boolean approve(BigDecimal loanAmount, BigDecimal customerBalance);
}