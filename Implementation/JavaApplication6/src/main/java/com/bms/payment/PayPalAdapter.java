package com.bms.payment;

import java.math.BigDecimal;

/**
 * PayPalAdapter - Concrete Adapter for PayPal Payment Gateway
 * 
 * Adapts the PayPal API to the PaymentGateway interface.
 * Handles all PayPal-specific logic (conversion, API calls, response translation).
 * 
 * SOLID: Single Responsibility - PayPal adaptation only
 */
public class PayPalAdapter implements PaymentGateway {

    private static final String GATEWAY_NAME = "PayPal";
    private final String clientId;
    private final String clientSecret;

    public PayPalAdapter(String clientId, String clientSecret) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalArgumentException("Client secret cannot be null or empty");
        }
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) throws PaymentGatewayException {
        if (request == null) {
            throw new IllegalArgumentException("PaymentRequest cannot be null");
        }

        try {
            // Convert to PayPal Order format and call API
            // In production: paypalClient.createOrder(...)
            String paypalOrderId = "PAY-" + System.nanoTime();

            return new PaymentResponse(
                paypalOrderId,
                request.getTransactionId(),
                PaymentStatus.APPROVED,
                "Payment processed",
                GATEWAY_NAME,
                paypalOrderId,
                null
            );

        } catch (Exception e) {
            throw new PaymentGatewayException(
                "PayPal payment failed: " + e.getMessage(),
                GATEWAY_NAME,
                "PAYPAL_ERROR",
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
            // Convert to PayPal refund format and call API
            // In production: paypalClient.refundCapture(...)
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
                "PayPal refund failed: " + e.getMessage(),
                GATEWAY_NAME,
                "PAYPAL_REFUND_ERROR",
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
        // In production: verify credentials with PayPal
        return clientId != null && !clientId.isEmpty() &&
               clientSecret != null && !clientSecret.isEmpty();
    }
}
