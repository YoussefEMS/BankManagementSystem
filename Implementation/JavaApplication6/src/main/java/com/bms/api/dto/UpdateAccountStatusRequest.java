package com.bms.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountStatusRequest(
        @NotBlank String status,
        String reason,
        Integer actorId,
        String role) {
}
