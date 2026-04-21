package com.bms.observer.overdraft;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bms.domain.entity.OverdraftEvent;

public final class OverdraftNotificationService {
    private static final OverdraftNotificationService INSTANCE = new OverdraftNotificationService();

    private final List<OverdraftObserver> observers = new CopyOnWriteArrayList<>();

    private OverdraftNotificationService() {
        subscribe(new DatabaseOverdraftObserver());
        subscribe(new CustomerNotificationObserver());
        subscribe(new AuditLogObserver());
    }

    public static OverdraftNotificationService getInstance() {
        return INSTANCE;
    }

    public void subscribe(OverdraftObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(OverdraftObserver observer) {
        observers.remove(observer);
    }

    public void publish(OverdraftEvent event) {
        if (event == null) {
            return;
        }

        for (OverdraftObserver observer : observers) {
            observer.onOverdraft(event);
        }
    }
}
