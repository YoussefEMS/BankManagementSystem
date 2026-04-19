package com.bms.domain.controller;

import java.time.LocalDateTime;

/**
 * Aggregate interface for the Iterator Design Pattern.
 */
public interface TransactionCollection {
    /**
     * Creates an iterator over the collection with optional filters.
     * 
     * @param startDate optional start date (inclusive)
     * @param endDate optional end date (inclusive)
     * @param typeFilter optional type filter ("All" or specific type)
     * @return a TransactionIterator configured with the requested filters.
     */
    TransactionIterator createIterator(LocalDateTime startDate, LocalDateTime endDate, String typeFilter);
}
