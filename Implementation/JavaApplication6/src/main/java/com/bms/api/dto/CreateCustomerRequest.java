package com.bms.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        @NotBlank String fullName,
        @NotBlank String email,
        @NotBlank String nationalId,
        @NotBlank String password,
        String phone,
        String address,
        @NotBlank String accountType,
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialDeposit,
        String currency) {
}
