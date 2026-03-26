package com.bms.payment;

/**
 * PaymentGatewayFactory - Creates PaymentGateway adapters
 * 
 * Encapsulates adapter creation, hiding concrete implementations from clients.
 * Returns PaymentGateway interface only - Dependency Inversion Principle.
 * Single Responsibility: Create and return appropriate payment gateway adapter.
 */
public class PaymentGatewayFactory {

    public enum GatewayType {
        STRIPE, PAYPAL, SQUARE
    }

    /**
     * Creates a PaymentGateway adapter based on type and credentials.
     * 
     * @param type Gateway type (STRIPE, PAYPAL, SQUARE)
     * @param credentials Gateway-specific credentials in order
     * @return PaymentGateway interface (concrete type hidden from client)
     * @throws IllegalArgumentException if credentials are missing or invalid
     */
    public static PaymentGateway createGateway(GatewayType type, String... credentials) {
        switch (type) {
            case STRIPE:
                if (credentials.length == 0) {
                    throw new IllegalArgumentException("Stripe requires API key");
                }
                return new StripeAdapter(credentials[0]);

            case PAYPAL:
                if (credentials.length < 2) {
                    throw new IllegalArgumentException("PayPal requires clientId and clientSecret");
                }
                return new PayPalAdapter(credentials[0], credentials[1]);

            case SQUARE:
                if (credentials.length < 2) {
                    throw new IllegalArgumentException("Square requires accessToken and locationId");
                }
                return new SquareAdapter(credentials[0], credentials[1]);

            default:
                throw new IllegalArgumentException("Unknown gateway type: " + type);
        }
    }

    /**
     * Creates a PaymentGateway adapter from a configuration string.
     * Format: "GATEWAY_TYPE:credential1:credential2:..."
     * 
     * @param gatewayConfig Configuration string
     * @return PaymentGateway interface
     */
    public static PaymentGateway createGateway(String gatewayConfig) {
        String[] parts = gatewayConfig.split(":");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid gateway configuration: " + gatewayConfig);
        }

        GatewayType type = GatewayType.valueOf(parts[0].toUpperCase());
        String[] credentials = new String[parts.length - 1];
        System.arraycopy(parts, 1, credentials, 0, parts.length - 1);

        return createGateway(type, credentials);
    }
}
