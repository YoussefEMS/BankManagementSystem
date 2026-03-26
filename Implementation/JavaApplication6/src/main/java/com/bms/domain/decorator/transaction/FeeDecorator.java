package com.bms.domain.decorator.transaction;

import java.math.BigDecimal;

public class FeeDecorator extends TransactionProcessorDecorator {
    private final BigDecimal feeAmount;
    private final String feeDescription;

    public FeeDecorator(TransactionProcessor wrappedProcessor, BigDecimal feeAmount, String feeDescription) {
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
