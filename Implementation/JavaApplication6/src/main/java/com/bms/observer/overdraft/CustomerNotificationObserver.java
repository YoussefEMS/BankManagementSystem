package com.bms.observer.overdraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.domain.entity.OverdraftEvent;

public class CustomerNotificationObserver implements OverdraftObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerNotificationObserver.class);

    @Override
    public void onOverdraft(OverdraftEvent event) {
        LOGGER.info("Customer overdraft notification recorded for account {} and transaction {}.",
                event.getAccountNumber(), event.getTransactionId());
    }
}
