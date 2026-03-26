package com.bms.payment;

/**
 * Normalised response returned by every gateway adapter.
 * Adapters translate their vendor-specific responses into this uniform format.
 */
public class PaymentResponse {
    private final String gatewayTransactionId;
    private final String internalTransactionId;
    private final PaymentStatus status;
    private final String message;
    private final String gatewayName;
    private final String receiptId;
    private final String errorCode;

    public PaymentResponse(String gatewayTransactionId, String internalTransactionId,
                           PaymentStatus status, String message, String gatewayName,
                           String receiptId, String errorCode) {
        this.gatewayTransactionId = gatewayTransactionId;
        this.internalTransactionId = internalTransactionId;
        this.status = status;
        this.message = message;
        this.gatewayName = gatewayName;
        this.receiptId = receiptId;
        this.errorCode = errorCode;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public String getInternalTransactionId() {
        return internalTransactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }
}
