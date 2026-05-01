package com.bms.event.handler;

import java.util.logging.Logger;

import com.bms.event.DomainEvent;
import com.bms.event.bus.EventHandler;

public class AuditLogEventHandler implements EventHandler<DomainEvent> {
    private static final Logger logger = Logger.getLogger(AuditLogEventHandler.class.getName());

    @Override
    public void handle(DomainEvent event) {
        logger.info("AUDIT " + event);
    }
}
