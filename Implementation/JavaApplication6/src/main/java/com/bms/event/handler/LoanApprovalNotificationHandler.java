package com.bms.event.handler;

import java.util.logging.Logger;

import com.bms.event.LoanApplicationSubmittedEvent;
import com.bms.event.bus.EventHandler;

public class LoanApprovalNotificationHandler implements EventHandler<LoanApplicationSubmittedEvent> {
    private static final Logger logger = Logger.getLogger(LoanApprovalNotificationHandler.class.getName());

    @Override
    public void handle(LoanApplicationSubmittedEvent event) {
        logger.info("Loan application " + event.getLoanId()
                + " submitted for customer " + event.getCustomerId()
                + " with status " + event.getStatus());
    }
}
