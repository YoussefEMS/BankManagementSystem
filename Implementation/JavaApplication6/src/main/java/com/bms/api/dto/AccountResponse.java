package com.bms.api.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        int customerId,
        String accountType,
        BigDecimal balance,
        String currency,
        String status) {
}
