package com.bms.domain.controller;


import com.bms.domain.entity.TransactionContext;
@FunctionalInterface
public interface TransactionOperation<T> {
    T execute(TransactionContext context);
}
