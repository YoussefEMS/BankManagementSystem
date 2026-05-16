package com.bms.api.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerOnboardingRequest(
        @Valid @NotNull CustomerData customer,
        @Valid @NotNull AccountData account,
        @Valid LoanApplicationData loanApplication) {

    public record CustomerData(
            @NotBlank String fullName,
            @NotBlank String email,
            @NotBlank String nationalId,
            @NotBlank String password,
            String phone,
            String address) {
    }

    public record AccountData(
            @NotBlank String accountType,
            @NotNull @DecimalMin(value = "0.00") BigDecimal initialDeposit,
            String currency) {
    }

    public record LoanApplicationData(
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotNull @Min(1) Integer termMonths,
            @NotBlank String loanPurpose,
            String loanType) {
    }
}
