package com.bms.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitLoanRequest(
        @NotNull @Min(1) Integer customerId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull @Min(1) Integer termMonths,
        @NotBlank String loanPurpose,
        String loanType) {
}
