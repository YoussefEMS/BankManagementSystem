package com.bms.api.dto;

import java.math.BigDecimal;

public record LoanResponse(
        int loanId,
        int customerId,
        BigDecimal amount,
        String loanType,
        String status,
        int termMonths,
        String loanPurpose) {
}
