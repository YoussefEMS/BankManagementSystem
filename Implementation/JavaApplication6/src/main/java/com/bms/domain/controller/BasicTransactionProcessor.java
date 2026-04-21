package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
public class BasicTransactionProcessor implements TransactionProcessor {
    @Override
    public <T> T process(TransactionContext context, TransactionOperation<T> operation) {
        return operation.execute(context);
    }
}
