package com.bms.payment;

/**
 * Normalised exception thrown by all gateway adapters.
 * Wraps vendor-specific errors into a uniform structure for the domain layer.
 */
public class PaymentGatewayException extends Exception {
    private final String gatewayName;
    private final String errorCode;
    private final int httpStatusCode;

    public PaymentGatewayException(String message, String gatewayName,
                                   String errorCode, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.gatewayName = gatewayName;
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
