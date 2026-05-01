package com.bms.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base type for immutable business events emitted by application services.
 */
public abstract class DomainEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final int version;

    protected DomainEvent(String aggregateId) {
        this(aggregateId, 1);
    }

    protected DomainEvent(String aggregateId, int version) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = aggregateId != null ? aggregateId : "";
        this.version = version;
    }

    public UUID getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }

    public String getEventType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getEventType() + "{eventId=" + eventId
                + ", aggregateId='" + aggregateId + '\''
                + ", version=" + version
                + ", timestamp=" + timestamp
                + '}';
    }
}
