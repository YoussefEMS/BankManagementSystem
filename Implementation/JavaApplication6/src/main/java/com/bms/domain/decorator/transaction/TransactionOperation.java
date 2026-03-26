package com.bms.domain.decorator.transaction;

@FunctionalInterface
public interface TransactionOperation<T> {
    T execute(TransactionContext context);
}
