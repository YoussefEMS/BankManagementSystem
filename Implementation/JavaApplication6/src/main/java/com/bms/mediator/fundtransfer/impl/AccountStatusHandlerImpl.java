package com.bms.mediator.fundtransfer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.mediator.fundtransfer.FundTransferContext;
import com.bms.mediator.fundtransfer.peers.IAccountStatusHandlerPeer;

public class AccountStatusHandlerImpl implements IAccountStatusHandlerPeer {
    private static final Logger logger = Logger.getLogger(AccountStatusHandlerImpl.class.getName());
    
    private final Map<String, AccountStatus> accountStatuses;

    public AccountStatusHandlerImpl() {
        this.accountStatuses = initializeStatuses();
    }

    private Map<String, AccountStatus> initializeStatuses() {
        Map<String, AccountStatus> statuses = new HashMap<>();
        statuses.put("ACC001", new AccountStatus("ACTIVE", false));
        statuses.put("ACC002", new AccountStatus("ACTIVE", false));
        statuses.put("ACC003", new AccountStatus("INACTIVE", false));
        statuses.put("ACC004", new AccountStatus("FROZEN", false));
        statuses.put("ACC005", new AccountStatus("ACTIVE", true));
        return statuses;
    }

    @Override
    public boolean validateAccountsStatus(String sourceAccountNo, String destinationAccountNo, FundTransferContext context) {
        AccountStatus sourceStatus = accountStatuses.get(sourceAccountNo);
        AccountStatus destStatus = accountStatuses.get(destinationAccountNo);

        if (sourceStatus == null || destStatus == null) return false;

        boolean sourceValid = sourceStatus.getStatus().equals("ACTIVE");
        boolean destValid = destStatus.getStatus().equals("ACTIVE");

        logger.info("Account status validation: Source=" + sourceAccountNo + ":" + sourceStatus.getStatus() +
                   ", Dest=" + destinationAccountNo + ":" + destStatus.getStatus());

        return sourceValid && destValid;
    }

    @Override
    public String getAccountStatus(String accountNo) {
        AccountStatus status = accountStatuses.get(accountNo);
        return status != null ? status.getStatus() : "UNKNOWN";
    }

    @Override
    public boolean hasTransferRestrictions(String accountNo) {
        AccountStatus status = accountStatuses.get(accountNo);
        boolean hasRestrictions = status != null && status.hasTransferRestriction();
        logger.info("Transfer restrictions check: " + accountNo + " = " + hasRestrictions);
        return hasRestrictions;
    }

    private static class AccountStatus {
        private final String status;
        private final boolean transferRestriction;

        AccountStatus(String status, boolean transferRestriction) {
            this.status = status;
            this.transferRestriction = transferRestriction;
        }

        public String getStatus() { return status; }
        public boolean hasTransferRestriction() { return transferRestriction; }
    }
}
