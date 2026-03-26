package com.bms.domain.decorator.transaction;

public interface TransactionProcessor {
    <T> T process(TransactionContext context, TransactionOperation<T> operation);
}
