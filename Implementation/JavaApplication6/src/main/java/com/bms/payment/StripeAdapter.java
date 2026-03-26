package com.bms.payment;

import java.math.BigDecimal;

/**
 * StripeAdapter - Concrete Adapter for Stripe Payment Gateway
 * 
 * Adapts the Stripe API to the PaymentGateway interface.
 * Handles all Stripe-specific logic (conversion, API calls, response translation).
 * 
 * SOLID: Single Responsibility - Stripe adaptation only
 */
public class StripeAdapter implements PaymentGateway {

    private static final String GATEWAY_NAME = "Stripe";
    private final String apiKey;

    public StripeAdapter(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        this.apiKey = apiKey;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) throws PaymentGatewayException {
        if (request == null) {
            throw new IllegalArgumentException("PaymentRequest cannot be null");
        }

        try {
            // Convert to Stripe format and call API
            // In production: com.stripe.model.Charge.create(...)
            long amountCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            String stripeTransactionId = "ch_" + System.nanoTime();

            return new PaymentResponse(
                stripeTransactionId,
                request.getTransactionId(),
                PaymentStatus.APPROVED,
                "Payment processed",
                GATEWAY_NAME,
                stripeTransactionId,
                null
            );

        } catch (Exception e) {
            throw new PaymentGatewayException(
                "Stripe payment failed: " + e.getMessage(),
                GATEWAY_NAME,
                "STRIPE_ERROR",
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
            // Convert to Stripe refund format and call API
            // In production: Charge.retrieve(...).refund(...)
            String refundId = "re_" + System.nanoTime();

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
                "Stripe refund failed: " + e.getMessage(),
                GATEWAY_NAME,
                "STRIPE_REFUND_ERROR",
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
        // In production: verify API key with Stripe
        return apiKey != null && !apiKey.isEmpty();
    }
}
