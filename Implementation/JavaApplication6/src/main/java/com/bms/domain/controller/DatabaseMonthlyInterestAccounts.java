package com.bms.domain.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.bms.domain.entity.MonthlyInterestContext;
import com.bms.domain.controller.MonthlyInterestAccounts;


public class DatabaseMonthlyInterestAccounts implements MonthlyInterestAccounts {
    private static final Logger logger = Logger.getLogger(DatabaseMonthlyInterestAccounts.class.getName());
    
    // Mock database of accounts for demo purposes
    private final Map<String, AccountData> accounts;

    public DatabaseMonthlyInterestAccounts() {
        this.accounts = initializeMockAccounts();
    }

    private Map<String, AccountData> initializeMockAccounts() {
        Map<String, AccountData> mockAccounts = new HashMap<>();
        
        mockAccounts.put("ACC001", new AccountData("ACC001", "SAVINGS", 10000.0, true));
        mockAccounts.put("ACC002", new AccountData("ACC002", "CURRENT", 50000.0, true));
        mockAccounts.put("ACC003", new AccountData("ACC003", "MONEY_MARKET", 100000.0, true));
        mockAccounts.put("ACC004", new AccountData("ACC004", "SAVINGS", 5000.0, false));
        mockAccounts.put("ACC005", new AccountData("ACC005", "INVESTMENT", 250000.0, true));
        
        return mockAccounts;
    }

    @Override
    public List<String> getActiveAccounts(MonthlyInterestContext context) {
        logger.info("Fetching active accounts");
        return accounts.values().stream()
                .filter(AccountData::isActive)
                .map(AccountData::getAccountNumber)
                .toList();
    }

    @Override
    public String getAccountType(String accountNo) {
        AccountData account = accounts.get(accountNo);
        if (account == null) {
            logger.warning("Account not found: " + accountNo);
            return null;
        }
        return account.getAccountType();
    }

    @Override
    public double getAccountBalance(String accountNo) {
        AccountData account = accounts.get(accountNo);
        if (account == null) {
            logger.warning("Account not found: " + accountNo);
            return 0.0;
        }
        return account.getBalance();
    }


    private static class AccountData {
        private final String accountNumber;
        private final String accountType;
        private double balance;
        private final boolean active;

        AccountData(String accountNumber, String accountType, double balance, boolean active) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.balance = balance;
            this.active = active;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        public boolean isActive() { return active; }
    }
}
