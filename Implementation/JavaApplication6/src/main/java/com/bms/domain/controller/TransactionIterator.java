package com.bms.domain.controller;

import com.bms.domain.entity.Transaction;

/**
 * Iterator interface for the Iterator Design Pattern.
 */
public interface TransactionIterator {
    /**
     * Checks if there are more transactions in the collection.
     * @return true if there are more transactions, false otherwise.
     */
    boolean hasNext();

    /**
     * Retrieves the next transaction in the collection.
     * @return the next Transaction.
     */
    Transaction next();
}
