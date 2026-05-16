package com.bms.api.dto;

public record SubmitLoanResponse(
        int loanId,
        String status,
        String message) {
}
