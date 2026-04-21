package com.bms.domain.controller;

import com.bms.domain.entity.OverdraftEvent;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.OverdraftEventDAO;

public class OverdraftEventRecorder implements OverdraftAlertListener {
    private final OverdraftEventDAO overdraftEventDAO;

    public OverdraftEventRecorder() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public OverdraftEventRecorder(PersistenceProvider factory) {
        this.overdraftEventDAO = factory.createOverdraftEventDAO();
    }

    @Override
    public void onOverdraft(OverdraftEvent event) {
        int overdraftId = overdraftEventDAO.insert(event);
        if (overdraftId > 0) {
            event.setOverdraftId(overdraftId);
        }
    }
}
