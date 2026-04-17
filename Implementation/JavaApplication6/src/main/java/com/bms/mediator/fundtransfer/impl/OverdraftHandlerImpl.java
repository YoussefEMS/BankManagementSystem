package com.bms.mediator.fundtransfer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.mediator.fundtransfer.FundTransferContext;
import com.bms.mediator.fundtransfer.peers.IOverdraftHandlerPeer;

public class OverdraftHandlerImpl implements IOverdraftHandlerPeer {
    private static final Logger logger = Logger.getLogger(OverdraftHandlerImpl.class.getName());
    
    private final Map<String, OverdraftInfo> overdraftLimits;

    public OverdraftHandlerImpl() {
        this.overdraftLimits = initializeOverdraftInfo();
    }

    private Map<String, OverdraftInfo> initializeOverdraftInfo() {
        Map<String, OverdraftInfo> limits = new HashMap<>();
        limits.put("ACC001", new OverdraftInfo(true, 10000.0));
        limits.put("ACC002", new OverdraftInfo(true, 25000.0));
        limits.put("ACC003", new OverdraftInfo(false, 0.0));
        limits.put("ACC004", new OverdraftInfo(true, 5000.0));
        limits.put("ACC005", new OverdraftInfo(true, 50000.0));
        return limits;
    }

    @Override
    public boolean isOverdraftAllowed(String accountNo) {
        OverdraftInfo info = overdraftLimits.get(accountNo);
        boolean allowed = info != null && info.isAllowed();
        logger.info("Overdraft allowed check: " + accountNo + " = " + allowed);
        return allowed;
    }

    @Override
    public boolean isWithinOverdraftLimit(String accountNo, double transferAmount, double currentBalance, FundTransferContext context) {
        OverdraftInfo info = overdraftLimits.get(accountNo);
        if (info == null || !info.isAllowed()) return false;

        double newBalance = currentBalance - transferAmount;
        boolean withinLimit = newBalance >= -info.getLimit();

        logger.info("Overdraft limit check: " + accountNo + 
                   ", Current=" + currentBalance + 
                   ", Transfer=" + transferAmount + 
                   ", NewBalance=" + newBalance + 
                   ", Limit=" + (-info.getLimit()) + 
                   ", Result=" + withinLimit);

        return withinLimit;
    }

    @Override
    public double getOverdraftLimit(String accountNo) {
        OverdraftInfo info = overdraftLimits.get(accountNo);
        return info != null ? info.getLimit() : 0.0;
    }

    @Override
    public boolean willTriggerOverdraft(String accountNo, double newBalance) {
        OverdraftInfo info = overdraftLimits.get(accountNo);
        if (info == null) return false;

        boolean willTrigger = newBalance < 0;
        logger.info("Will trigger overdraft: " + accountNo + " = " + willTrigger);
        return willTrigger;
    }

    private static class OverdraftInfo {
        private final boolean allowed;
        private final double limit;

        OverdraftInfo(boolean allowed, double limit) {
            this.allowed = allowed;
            this.limit = limit;
        }

        public boolean isAllowed() { return allowed; }
        public double getLimit() { return limit; }
    }
}
