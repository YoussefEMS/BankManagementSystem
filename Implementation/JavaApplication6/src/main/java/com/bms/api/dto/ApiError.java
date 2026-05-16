package com.bms.api.dto;

import java.util.List;

public record ApiError(
        String code,
        String message,
        List<String> details) {
}
