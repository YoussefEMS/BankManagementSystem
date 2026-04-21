package com.bms.domain.controller;

import java.math.BigDecimal;

public interface LoanApprovalPolicy {
    boolean approve(BigDecimal loanAmount, BigDecimal customerBalance);
}