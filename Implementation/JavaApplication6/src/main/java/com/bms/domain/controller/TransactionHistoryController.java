package com.bms.domain.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bms.domain.entity.Transaction;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.TransactionDAO;

/**
 * TransactionHistoryController - Domain logic for viewing transaction history
 * Implements UC-04: View Transaction History
 * No thrown errors for not-found/empty cases - returns empty list instead
 */
public class TransactionHistoryController {
    private final TransactionDAO transactionDAO;

    public TransactionHistoryController() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public TransactionHistoryController(DAOFactory factory) {
        this.transactionDAO = factory.createTransactionDAO();
    }

    /**
     * View transaction history for an account with optional filters
     * 
     * @param accountNo  the account number to search for
     * @param startDate  optional start date (inclusive, time set to 00:00:00)
     * @param endDate    optional end date (inclusive, time set to 23:59:59)
     * @param typeFilter optional type filter ("All" or specific type)
     * @return List of transactions, empty list if no results (no error thrown)
     */
    public List<Transaction> viewTransactionHistory(String accountNo,
            LocalDate startDate,
            LocalDate endDate,
            String typeFilter) {
        // Input validation: allow free-text but trim whitespace
        if (accountNo == null || accountNo.trim().isEmpty()) {
            return List.of(); // Return empty list for invalid input
        }

        // Convert LocalDate ranges to LocalDateTime
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay(); // 00:00:00
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(LocalTime.MAX); // 23:59:59
        }

        // Normalize typeFilter: treat null or empty as "All"
        String normalizedFilter = typeFilter;
        if (typeFilter == null || typeFilter.trim().isEmpty()) {
            normalizedFilter = "All";
        }

        // Call DAO to retrieve transactions
        List<Transaction> transactions = transactionDAO.findByAccountNo(
                accountNo.trim(),
                startDateTime,
                endDateTime,
                normalizedFilter);

        // Return list (empty or populated, no error handling)
        return transactions;
    }

    /**
     * Get transaction history for presentation layer as string arrays
     * 
     * @param accountNo  the account number to search for
     * @param startDate  optional start date
     * @param endDate    optional end date
     * @param typeFilter optional type filter
     * @return 2D array where each row is [timestamp, type, amount, note,
     *         balanceAfter]
     *         Returns empty array if no transactions
     */
    public String[][] getTransactionHistoryAsStrings(String accountNo,
            LocalDate startDate,
            LocalDate endDate,
            String typeFilter) {
        List<Transaction> transactions = viewTransactionHistory(accountNo, startDate, endDate, typeFilter);
        String[][] transactionsData = new String[transactions.size()][5];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            transactionsData[i][0] = transaction.getTimestamp() != null ? transaction.getTimestamp().format(formatter)
                    : "";
            transactionsData[i][1] = transaction.getType() != null ? transaction.getType() : "";
            transactionsData[i][2] = transaction.getAmount() != null ? String.format("%.2f", transaction.getAmount())
                    : "";
            transactionsData[i][3] = transaction.getNote() != null ? transaction.getNote() : "";
            transactionsData[i][4] = transaction.getBalanceAfter() != null
                    ? String.format("%.2f", transaction.getBalanceAfter())
                    : "";
        }

        return transactionsData;
    }
}
