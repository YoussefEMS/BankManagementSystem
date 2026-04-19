package com.bms.domain.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import com.bms.domain.entity.Transaction;

/**
 * Concrete Iterator implementing the Iterator pattern.
 * Traverses a list of transactions, efficiently finding and returning 
 * only the elements that match the given filter criteria.
 */
public class FilteredTransactionIterator implements TransactionIterator {
    private final List<Transaction> transactions;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String typeFilter;
    
    private int currentIndex = 0;
    private Transaction nextMatchingTransaction = null;

    public FilteredTransactionIterator(List<Transaction> transactions, 
                                       LocalDateTime startDate, 
                                       LocalDateTime endDate, 
                                       String typeFilter) {
        this.transactions = transactions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.typeFilter = (typeFilter != null && !typeFilter.trim().isEmpty()) ? typeFilter : "All";
    }

    @Override
    public boolean hasNext() {
        // If we already peeked and found a match, there is a next element.
        if (nextMatchingTransaction != null) {
            return true;
        }
        
        // Scan forward to find the next matching transaction
        while (currentIndex < transactions.size()) {
            Transaction t = transactions.get(currentIndex++);
            if (matchesCriteria(t)) {
                nextMatchingTransaction = t;
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Transaction next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Transaction result = nextMatchingTransaction;
        // Reset the peeked element so the next call to hasNext() will advance
        nextMatchingTransaction = null;
        return result;
    }
    
    private boolean matchesCriteria(Transaction transaction) {
        // Check start date
        if (startDate != null && transaction.getTimestamp() != null) {
            if (transaction.getTimestamp().isBefore(startDate)) {
                return false;
            }
        }
        
        // Check end date
        if (endDate != null && transaction.getTimestamp() != null) {
            if (transaction.getTimestamp().isAfter(endDate)) {
                return false;
            }
        }
        
        // Check type
        if (!"All".equalsIgnoreCase(typeFilter)) {
            if (transaction.getType() == null || !typeFilter.equalsIgnoreCase(transaction.getType())) {
                return false;
            }
        }
        
        return true;
    }
}
