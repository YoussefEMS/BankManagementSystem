package com.bms.observer.overdraft;

import java.util.function.Consumer;

import com.bms.domain.entity.OverdraftEvent;

public class AdminAlertObserver implements OverdraftObserver {
    private final Consumer<OverdraftEvent> callback;

    public AdminAlertObserver(Consumer<OverdraftEvent> callback) {
        this.callback = callback;
    }

    @Override
    public void onOverdraft(OverdraftEvent event) {
        if (callback != null) {
            callback.accept(event);
        }
    }
}
