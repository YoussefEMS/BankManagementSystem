package com.bms.domain.entity;

/**
 * Normalised payment status values used across all gateway adapters.
 */
public enum PaymentStatus {
    APPROVED,
    DECLINED,
    PENDING,
    REFUNDED,
    ERROR
}
