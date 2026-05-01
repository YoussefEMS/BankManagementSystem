package com.bms.service;

import java.time.LocalDate;

import com.bms.domain.controller.MonthlyInterestPostingCoordinator;
import com.bms.event.InterestPostedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.service.base.ApplicationService;

public class InterestPostingService extends ApplicationService {
    private final MonthlyInterestPostingCoordinator postingCoordinator;

    public InterestPostingService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public InterestPostingService(PersistenceProvider factory) {
        super();
        this.postingCoordinator = new MonthlyInterestPostingCoordinator(factory);
    }

    public InterestPostingService(MonthlyInterestPostingCoordinator postingCoordinator,
            EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.postingCoordinator = postingCoordinator;
    }

    public int postMonthlyInterest() {
        int accountCount = postingCoordinator.postMonthlyInterest();
        publish(new InterestPostedEvent(LocalDate.now(), accountCount));
        return accountCount;
    }
}
