package com.bms.event.handler;

import java.util.logging.Logger;

import com.bms.event.OverdraftDetectedEvent;
import com.bms.event.bus.EventHandler;

public class OverdraftAlertHandler implements EventHandler<OverdraftDetectedEvent> {
    private static final Logger logger = Logger.getLogger(OverdraftAlertHandler.class.getName());

    @Override
    public void handle(OverdraftDetectedEvent event) {
        logger.warning("Overdraft detected for account " + event.getAccountNumber()
                + " with balance " + event.getBalance());
    }
}
