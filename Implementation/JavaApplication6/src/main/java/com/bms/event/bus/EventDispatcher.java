package com.bms.event.bus;

import com.bms.event.DomainEvent;

public interface EventDispatcher {
    <T extends DomainEvent> void subscribe(Class<T> eventType, EventHandler<? super T> handler);

    void dispatch(DomainEvent event);
}
