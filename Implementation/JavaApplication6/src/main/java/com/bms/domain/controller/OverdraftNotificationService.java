package com.bms.domain.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bms.domain.entity.OverdraftEvent;

public final class OverdraftNotificationService {
    private static final OverdraftNotificationService INSTANCE = new OverdraftNotificationService();

    private final List<OverdraftAlertListener> observers = new CopyOnWriteArrayList<>();

    private OverdraftNotificationService() {
        subscribe(new OverdraftEventRecorder());
        subscribe(new CustomerOverdraftNotifier());
        subscribe(new OverdraftAuditLogger());
    }

    public static OverdraftNotificationService getInstance() {
        return INSTANCE;
    }

    public void subscribe(OverdraftAlertListener observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(OverdraftAlertListener observer) {
        observers.remove(observer);
    }

    public void publish(OverdraftEvent event) {
        if (event == null) {
            return;
        }

        for (OverdraftAlertListener observer : observers) {
            observer.onOverdraft(event);
        }
    }
}
