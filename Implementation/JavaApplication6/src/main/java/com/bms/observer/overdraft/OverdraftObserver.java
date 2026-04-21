package com.bms.observer.overdraft;

import com.bms.domain.entity.OverdraftEvent;

public interface OverdraftObserver {
    void onOverdraft(OverdraftEvent event);
}
