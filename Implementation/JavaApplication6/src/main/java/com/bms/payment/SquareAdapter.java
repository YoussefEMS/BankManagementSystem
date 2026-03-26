package com.bms.payment;

import java.math.BigDecimal;

/**
 * SquareAdapter - Concrete Adapter for Square Payment Gateway
 * 
 * Adapts the Square API to the PaymentGateway interface.
 * Handles all Square-specific logic (conversion, API calls, response translation).
 * 
 * SOLID: Single Responsibility - Square adaptation only
 */
public class SquareAdapter implements PaymentGateway {

    private static final String GATEWAY_NAME = "Square";
    private final String accessToken;
    private final String locationId;

    public SquareAdapter(String accessToken, String locationId) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        if (locationId == null || locationId.isEmpty()) {
            throw new IllegalArgumentException("Location ID cannot be null or empty");
        }
        this.accessToken = accessToken;
        this.locationId = locationId;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) throws PaymentGatewayException {
        if (request == null) {
            throw new IllegalArgumentException("PaymentRequest cannot be null");
        }

        try {
            // Convert to Square Payment format and call API
            // In production: squareClient.getPaymentsAPI().createPayment(...)
            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            String squarePaymentId = "SQ-" + System.nanoTime();

            return new PaymentResponse(
                squarePaymentId,
                request.getTransactionId(),
                PaymentStatus.APPROVED,
                "Payment processed",
                GATEWAY_NAME,
                squarePaymentId,
                null
            );

        } catch (Exception e) {
            throw new PaymentGatewayException(
                "Square payment failed: " + e.getMessage(),
                GATEWAY_NAME,
                "SQUARE_ERROR",
                500,
                e
            );
        }
    }

    @Override
    public PaymentResponse refundPayment(String gatewayTransactionId, BigDecimal amount)
            throws PaymentGatewayException {
        if (gatewayTransactionId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }

        try {
            // Convert to Square Refund format and call API
            // In production: squareClient.getRefundsAPI().refundPayment(...)
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            String refundId = "REF-" + System.nanoTime();

            return new PaymentResponse(
                gatewayTransactionId,
                null,
                PaymentStatus.REFUNDED,
                "Refund processed",
                GATEWAY_NAME,
                refundId,
                null
            );

        } catch (Exception e) {
            throw new PaymentGatewayException(
                "Square refund failed: " + e.getMessage(),
                GATEWAY_NAME,
                "SQUARE_REFUND_ERROR",
                500,
                e
            );
        }
    }

    @Override
    public String getGatewayName() {
        return GATEWAY_NAME;
    }

    @Override
    public boolean isHealthy() {
        // In production: verify credentials with Square
        return accessToken != null && !accessToken.isEmpty() &&
               locationId != null && !locationId.isEmpty();
    }
}
