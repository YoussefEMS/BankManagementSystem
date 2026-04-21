package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
public class AuditedTransactionProcessor extends TransactionProcessorChain {
    public AuditedTransactionProcessor(TransactionProcessor wrappedProcessor) {
        super(wrappedProcessor);
    }

    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        context.addAuditEntry("Audit start: " + context.getTransactionType() + " for " + context.getAccountNumber());
        T result = super.process(context, operation);
        context.addAuditEntry("Audit complete: " + context.getTransactionType());
        for (String entry : context.getAuditEntries()) {
            System.out.println(entry);
        }
        return result;
    }
}
