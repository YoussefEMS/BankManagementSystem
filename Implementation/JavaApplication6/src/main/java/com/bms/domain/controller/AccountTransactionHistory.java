package com.bms.domain.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.Transaction;

/**
 * Concrete Aggregate implementing the Iterator pattern.
 * Maintains the internal collection of a single account's transactions.
 */
public class AccountTransactionHistory implements TransactionCollection {
    private final List<Transaction> accountTransactions;

    public AccountTransactionHistory(List<Transaction> accountTransactions) {
        if (accountTransactions == null) {
            this.accountTransactions = List.of();
        } else {
            this.accountTransactions = accountTransactions;
        }
    }

    @Override
    public TransactionIterator createIterator(LocalDateTime startDate, LocalDateTime endDate, String typeFilter) {
        return new FilteredTransactionIterator(accountTransactions, startDate, endDate, typeFilter);
    }
}
