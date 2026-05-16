package com.bms.api.dto;

public record CreateCustomerResponse(
        int customerId,
        String accountNumber,
        String status) {
}
