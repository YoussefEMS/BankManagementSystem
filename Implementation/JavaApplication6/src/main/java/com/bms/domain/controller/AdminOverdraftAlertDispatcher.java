package com.bms.domain.controller;

import java.util.function.Consumer;

import com.bms.domain.entity.OverdraftEvent;

public class AdminOverdraftAlertDispatcher implements OverdraftAlertListener {
    private final Consumer<OverdraftEvent> callback;

    public AdminOverdraftAlertDispatcher(Consumer<OverdraftEvent> callback) {
        this.callback = callback;
    }

    @Override
    public void onOverdraft(OverdraftEvent event) {
        if (callback != null) {
            callback.accept(event);
        }
    }
}
