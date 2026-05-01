package com.bms.event.handler;

import com.bms.event.DomainEvent;
import com.bms.event.FundTransferCompletedEvent;
import com.bms.event.LoanApplicationSubmittedEvent;
import com.bms.event.OverdraftDetectedEvent;
import com.bms.event.bus.EventDispatcher;

public final class EventHandlerRegistry {
    private EventHandlerRegistry() {
    }

    public static void registerDefaults(EventDispatcher dispatcher) {
        dispatcher.subscribe(DomainEvent.class, new AuditLogEventHandler());
        dispatcher.subscribe(LoanApplicationSubmittedEvent.class, new LoanApprovalNotificationHandler());
        dispatcher.subscribe(FundTransferCompletedEvent.class, new TransactionConfirmationHandler());
        dispatcher.subscribe(OverdraftDetectedEvent.class, new OverdraftAlertHandler());
    }
}
