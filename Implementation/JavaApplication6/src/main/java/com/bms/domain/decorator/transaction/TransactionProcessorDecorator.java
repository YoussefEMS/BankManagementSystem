package com.bms.domain.decorator.transaction;

public abstract class TransactionProcessorDecorator implements TransactionProcessor {
    protected final TransactionProcessor wrappedProcessor;

    protected TransactionProcessorDecorator(TransactionProcessor wrappedProcessor) {
        this.wrappedProcessor = wrappedProcessor;
    }

    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        return wrappedProcessor.process(context, operation);
    }
}
