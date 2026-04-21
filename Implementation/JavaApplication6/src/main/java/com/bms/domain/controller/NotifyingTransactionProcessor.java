package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
public class NotifyingTransactionProcessor extends TransactionProcessorChain {
    public NotifyingTransactionProcessor(TransactionProcessor wrappedProcessor) {
        super(wrappedProcessor);
    }

    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        T result = super.process(context, operation);
        System.out.println("Notification sent for " + context.getTransactionType()
                + " on account " + context.getAccountNumber() + ".");
        return result;
    }
}
