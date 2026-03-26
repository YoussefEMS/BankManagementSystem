package com.bms.domain.decorator.transaction;

public class BasicTransactionProcessor implements TransactionProcessor {
    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        return operation.execute(context);
    }
}
