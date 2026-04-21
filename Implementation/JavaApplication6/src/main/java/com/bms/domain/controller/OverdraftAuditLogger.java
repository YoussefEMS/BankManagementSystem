package com.bms.domain.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.domain.entity.OverdraftEvent;

public class OverdraftAuditLogger implements OverdraftAlertListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverdraftAuditLogger.class);

    @Override
    public void onOverdraft(OverdraftEvent event) {
        LOGGER.info("Overdraft audit: account={}, transactionId={}, amount={}, timestamp={}.",
                event.getAccountNumber(),
                event.getTransactionId(),
                event.getAmount(),
                event.getTimestamp());
    }
}
