package com.bms.domain.controller;

import com.bms.domain.entity.OverdraftEvent;

public interface OverdraftAlertListener {
    void onOverdraft(OverdraftEvent event);
}
