package com.bms.api.dto;

public record CustomerOnboardingResponse(
        int customerId,
        String accountNumber,
        Integer loanId,
        String workflowStatus) {
}
