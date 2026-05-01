package com.bms.event.bus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bms.event.DomainEvent;

public class SynchronousEventDispatcher implements EventDispatcher {
    private static final Logger logger = Logger.getLogger(SynchronousEventDispatcher.class.getName());

    private final Map<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>> handlers =
            new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void subscribe(Class<T> eventType, EventHandler<? super T> handler) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");
        handlers.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>())
                .add((EventHandler<? extends DomainEvent>) (EventHandler<?>) handler);
    }

    @Override
    public void dispatch(DomainEvent event) {
        Objects.requireNonNull(event, "event");
        for (Map.Entry<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>> entry
                : handlers.entrySet()) {
            if (!entry.getKey().isAssignableFrom(event.getClass())) {
                continue;
            }
            for (EventHandler<? extends DomainEvent> handler : entry.getValue()) {
                notifyHandler(handler, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void notifyHandler(EventHandler<T> handler, DomainEvent event) {
        try {
            handler.handle((T) event);
        } catch (RuntimeException ex) {
            logger.log(Level.WARNING, "Event handler failed for " + event.getEventType(), ex);
        }
    }
}
