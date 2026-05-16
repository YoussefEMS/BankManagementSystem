package com.bms.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanDecisionRequest(
        @NotBlank String decision,
        @NotNull Integer adminId,
        String reason) {
}
