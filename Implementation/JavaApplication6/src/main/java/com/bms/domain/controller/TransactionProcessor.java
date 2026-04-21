package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
public interface TransactionProcessor {
    <T> T process(TransactionContext context, TransactionOperation<T> operation);
}
