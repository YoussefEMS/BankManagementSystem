package com.bms.api.dto;

public record LoanCancelResponse(
        int loanId,
        String status,
        String message) {
}
