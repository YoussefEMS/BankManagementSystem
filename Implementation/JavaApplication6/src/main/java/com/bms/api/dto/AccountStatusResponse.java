package com.bms.api.dto;

public record AccountStatusResponse(
        String accountNumber,
        String status,
        String message) {
}
