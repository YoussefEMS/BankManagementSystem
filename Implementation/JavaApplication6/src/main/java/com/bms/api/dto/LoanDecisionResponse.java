package com.bms.api.dto;

public record LoanDecisionResponse(
        int loanId,
        String status,
        String message) {
}
