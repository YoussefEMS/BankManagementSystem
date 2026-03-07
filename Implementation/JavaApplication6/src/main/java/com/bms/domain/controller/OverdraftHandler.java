package com.bms.domain.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.OverdraftEvent;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.OverdraftEventDAO;
import com.bms.persistence.SqlServerDAOFactory;

/**
 * OverdraftHandler - UC-13: Overdraft Detection & Alerts
 * Checks for overdraft conditions and records events
 */
public class OverdraftHandler {
    private final OverdraftEventDAO overdraftEventDAO;

    public OverdraftHandler() {
        this(SqlServerDAOFactory.getInstance());
    }

    public OverdraftHandler(DAOFactory factory) {
        this.overdraftEventDAO = factory.createOverdraftEventDAO();
    }

    /**
     * Check if a transaction resulted in an overdraft and record it
     * 
     * @param accountNo     the account number
     * @param newBalance    the new balance after the transaction
     * @param transactionId the transaction that caused the overdraft
     * @param timestamp     when the overdraft occurred
     */
    public void checkOverdraft(String accountNo, double newBalance,
            int transactionId, LocalDateTime timestamp) {
        if (newBalance < 0) {
            OverdraftEvent event = new OverdraftEvent();
            event.setAccountNumber(accountNo);
            event.setTransactionId(transactionId);
            event.setAmount(Math.abs(newBalance));
            event.setTimestamp(timestamp != null ? timestamp : LocalDateTime.now());
            event.setAlertSent(true); // Mark as alert sent
            overdraftEventDAO.insert(event);
        }
    }

    /**
     * Get all overdraft events (for admin view)
     */
    public List<OverdraftEvent> getAllOverdraftEvents() {
        return overdraftEventDAO.findAll();
    }
}