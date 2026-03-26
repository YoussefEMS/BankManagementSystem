package com.bms.domain.decorator.transaction;

public class NotificationDecorator extends TransactionProcessorDecorator {
    public NotificationDecorator(TransactionProcessor wrappedProcessor) {
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
