package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
import java.math.BigDecimal;

public class FeeChargingTransactionProcessor extends TransactionProcessorChain {
    private final BigDecimal feeAmount;
    private final String feeDescription;

    public FeeChargingTransactionProcessor(TransactionProcessor wrappedProcessor, BigDecimal feeAmount, String feeDescription) {
        super(wrappedProcessor);
        this.feeAmount = feeAmount;
        this.feeDescription = feeDescription;
    }

    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        context.addFee(feeAmount, feeDescription);
        return super.process(context, operation);
    }
}
