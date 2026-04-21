package com.bms.domain.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.domain.entity.FundTransferContext;
import com.bms.domain.controller.SourceAccountValidator;

public class SourceAccountAvailabilityValidator implements SourceAccountValidator {
    private static final Logger logger = Logger.getLogger(SourceAccountAvailabilityValidator.class.getName());
    
    private final Map<String, AccountInfo> accounts;

    public SourceAccountAvailabilityValidator() {
        this.accounts = initializeAccounts();
    }

    private Map<String, AccountInfo> initializeAccounts() {
        Map<String, AccountInfo> mockAccounts = new HashMap<>();
        mockAccounts.put("ACC001", new AccountInfo("ACC001", 50000.0, true));
        mockAccounts.put("ACC002", new AccountInfo("ACC002", 100000.0, true));
        mockAccounts.put("ACC003", new AccountInfo("ACC003", 25000.0, true));
        mockAccounts.put("ACC004", new AccountInfo("ACC004", 5000.0, false));
        mockAccounts.put("ACC005", new AccountInfo("ACC005", 300000.0, true));
        return mockAccounts;
    }

    @Override
    public boolean validateSourceAccountExists(String accountNo, FundTransferContext context) {
        boolean exists = accounts.containsKey(accountNo);
        logger.info("Source account existence check: " + accountNo + " = " + exists);
        return exists;
    }

    @Override
    public boolean validateSufficientFunds(String accountNo, double transferAmount, FundTransferContext context) {
        AccountInfo account = accounts.get(accountNo);
        if (account == null) return false;

        boolean hasFunds = account.getBalance() >= transferAmount;
        logger.info("Sufficient funds check: Account=" + accountNo + 
                   ", Balance=" + account.getBalance() + 
                   ", Transfer=" + transferAmount + 
                   ", Result=" + hasFunds);
        return hasFunds;
    }

    @Override
    public double getSourceAccountBalance(String accountNo) {
        AccountInfo account = accounts.get(accountNo);
        return account != null ? account.getBalance() : 0.0;
    }

    private static class AccountInfo {
        private final String accountNo;
        private double balance;
        private final boolean active;

        AccountInfo(String accountNo, double balance, boolean active) {
            this.accountNo = accountNo;
            this.balance = balance;
            this.active = active;
        }

        public String getAccountNo() { return accountNo; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        public boolean isActive() { return active; }
    }
}
