package com.bms.domain.controller;



import com.bms.domain.entity.PaymentResponse;
import com.bms.domain.entity.PaymentRequest;
import java.math.BigDecimal;

/**
 * PaymentGateway - Target interface for the Adapter pattern.
 * 
 * Defines a uniform contract that the BMS domain layer programs against.
 * Each third-party payment provider (Stripe, PayPal, Square) has an
 * incompatible proprietary API; concrete adapters translate those APIs
 * into this common interface.
 */
public interface PaymentGateway {

    /**
     * Process a payment through this gateway.
     *
     * @param request the payment request containing amount, currency, etc.
     * @return a normalised PaymentResponse
     * @throws PaymentGatewayException if the gateway rejects or fails the payment
     */
    PaymentResponse processPayment(PaymentRequest request) throws PaymentGatewayException;

    /**
     * Refund a previously processed payment.
     *
     * @param gatewayTransactionId the gateway's original transaction identifier
     * @param amount               the amount to refund
     * @return a normalised PaymentResponse with REFUNDED status
     * @throws PaymentGatewayException if the refund fails
     */
    PaymentResponse refundPayment(String gatewayTransactionId, BigDecimal amount)
            throws PaymentGatewayException;

    /**
     * @return the human-readable name of this gateway (e.g. "Stripe")
     */
    String getGatewayName();

    /**
     * Health check — verifies credentials / connectivity.
     *
     * @return true if the gateway is reachable and credentials are valid
     */
    boolean isHealthy();
}
