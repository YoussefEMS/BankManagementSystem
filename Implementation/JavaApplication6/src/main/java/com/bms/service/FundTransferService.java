package com.bms.service;

import java.math.BigDecimal;

import com.bms.domain.controller.FundsTransferProcessor;
import com.bms.event.FundTransferCompletedEvent;
import com.bms.event.FundTransferFailedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.service.base.ApplicationService;

public class FundTransferService extends ApplicationService {
    private final FundsTransferProcessor fundsTransferProcessor;

    public FundTransferService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public FundTransferService(PersistenceProvider factory) {
        super();
        this.fundsTransferProcessor = new FundsTransferProcessor(factory);
    }

    public FundTransferService(FundsTransferProcessor fundsTransferProcessor, EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.fundsTransferProcessor = fundsTransferProcessor;
    }

    public String transferFunds(int customerId, String sourceAccountNo,
            String destinationAccountNo, double amount) {
        if (!Double.isFinite(amount)) {
            publish(new FundTransferFailedEvent(customerId, sourceAccountNo,
                    destinationAccountNo, BigDecimal.ZERO, "Amount must be finite"));
            return null;
        }

        String referenceCode = fundsTransferProcessor.transferFunds(
                customerId, sourceAccountNo, destinationAccountNo, amount);
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        if (referenceCode != null) {
            publish(new FundTransferCompletedEvent(customerId, sourceAccountNo,
                    destinationAccountNo, transferAmount, referenceCode));
        } else {
            publish(new FundTransferFailedEvent(customerId, sourceAccountNo,
                    destinationAccountNo, transferAmount, "Validation or processing failed"));
        }
        return referenceCode;
    }
}
