package com.bms.mediator.fundtransfer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.mediator.fundtransfer.FundTransferContext;
import com.bms.mediator.fundtransfer.peers.IDestinationAccountValidatorPeer;

public class DestinationAccountValidatorImpl implements IDestinationAccountValidatorPeer {
    private static final Logger logger = Logger.getLogger(DestinationAccountValidatorImpl.class.getName());
    
    private final Map<String, DestinationAccountInfo> accounts;

    public DestinationAccountValidatorImpl() {
        this.accounts = initializeAccounts();
    }

    private Map<String, DestinationAccountInfo> initializeAccounts() {
        Map<String, DestinationAccountInfo> mockAccounts = new HashMap<>();
        mockAccounts.put("ACC001", new DestinationAccountInfo("ACC001", true, false, 1));
        mockAccounts.put("ACC002", new DestinationAccountInfo("ACC002", true, false, 2));
        mockAccounts.put("ACC003", new DestinationAccountInfo("ACC003", false, false, 3));
        mockAccounts.put("ACC004", new DestinationAccountInfo("ACC004", true, true, 1));
        mockAccounts.put("ACC005", new DestinationAccountInfo("ACC005", true, false, 3));
        return mockAccounts;
    }

    @Override
    public boolean validateDestinationAccountExists(String accountNo, FundTransferContext context) {
        boolean exists = accounts.containsKey(accountNo);
        logger.info("Destination account existence check: " + accountNo + " = " + exists);
        return exists;
    }

    @Override
    public boolean validateDestinationCanReceiveFunds(String accountNo, FundTransferContext context) {
        DestinationAccountInfo account = accounts.get(accountNo);
        if (account == null) return false;

        boolean canReceive = account.isActive() && !account.isFrozen();
        logger.info("Destination can receive check: " + accountNo + " = " + canReceive);
        return canReceive;
    }

    @Override
    public boolean areSameCustomerAccounts(String sourceAccountNo, String destinationAccountNo) {
        DestinationAccountInfo srcAccount = accounts.get(sourceAccountNo);
        DestinationAccountInfo destAccount = accounts.get(destinationAccountNo);

        if (srcAccount == null || destAccount == null) return false;

        boolean sameCustomer = srcAccount.getCustomerId() == destAccount.getCustomerId();
        logger.info("Same customer check: " + sourceAccountNo + " and " + destinationAccountNo + 
                   " = " + sameCustomer);
        return sameCustomer;
    }

    private static class DestinationAccountInfo {
        private final String accountNo;
        private final boolean active;
        private final boolean frozen;
        private final int customerId;

        DestinationAccountInfo(String accountNo, boolean active, boolean frozen, int customerId) {
            this.accountNo = accountNo;
            this.active = active;
            this.frozen = frozen;
            this.customerId = customerId;
        }

        public String getAccountNo() { return accountNo; }
        public boolean isActive() { return active; }
        public boolean isFrozen() { return frozen; }
        public int getCustomerId() { return customerId; }
    }
}
