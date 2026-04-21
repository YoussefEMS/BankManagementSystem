package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
public abstract class TransactionProcessorChain implements TransactionProcessor {
    protected final TransactionProcessor wrappedProcessor;

    protected TransactionProcessorChain(TransactionProcessor wrappedProcessor) {
        this.wrappedProcessor = wrappedProcessor;
    }

    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        return wrappedProcessor.process(context, operation);
    }
}
