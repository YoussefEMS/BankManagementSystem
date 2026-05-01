package com.bms.service.base;

import java.util.Objects;

import com.bms.event.DomainEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.event.bus.SynchronousEventDispatcher;
import com.bms.event.handler.EventHandlerRegistry;

public abstract class ApplicationService {
    private static final EventDispatcher DEFAULT_DISPATCHER = createDefaultDispatcher();

    private final EventDispatcher eventDispatcher;

    protected ApplicationService() {
        this(DEFAULT_DISPATCHER);
    }

    protected ApplicationService(EventDispatcher eventDispatcher) {
        this.eventDispatcher = Objects.requireNonNull(eventDispatcher, "eventDispatcher");
    }

    protected void publish(DomainEvent event) {
        eventDispatcher.dispatch(event);
    }

    protected EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private static EventDispatcher createDefaultDispatcher() {
        EventDispatcher dispatcher = new SynchronousEventDispatcher();
        EventHandlerRegistry.registerDefaults(dispatcher);
        return dispatcher;
    }
}
