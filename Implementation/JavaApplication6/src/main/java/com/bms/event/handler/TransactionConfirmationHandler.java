package com.bms.event.handler;

import java.util.logging.Logger;

import com.bms.event.FundTransferCompletedEvent;
import com.bms.event.bus.EventHandler;

public class TransactionConfirmationHandler implements EventHandler<FundTransferCompletedEvent> {
    private static final Logger logger = Logger.getLogger(TransactionConfirmationHandler.class.getName());

    @Override
    public void handle(FundTransferCompletedEvent event) {
        logger.info("Transfer completed: " + event.getReferenceCode()
                + " from " + event.getSourceAccountNumber()
                + " to " + event.getDestinationAccountNumber());
    }
}
