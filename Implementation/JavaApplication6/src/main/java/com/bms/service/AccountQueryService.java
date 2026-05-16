package com.bms.service;

import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.TransactionDAO;
import com.bms.service.base.ApplicationService;

public class AccountQueryService extends ApplicationService {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AccountQueryService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public AccountQueryService(PersistenceProvider factory) {
        super();
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
    }

    public Account getAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return null;
        }
        return accountDAO.findByAccountNo(accountNumber.trim());
    }

    public List<Account> getAccountsForCustomer(int customerId) {
        if (customerId <= 0) {
            return List.of();
        }
        return accountDAO.findByCustomerId(customerId);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        return getTransactions(accountNumber, null, null, null);
    }

    public List<Transaction> getTransactions(String accountNumber, LocalDateTime startDate,
            LocalDateTime endDate, String typeFilter) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return List.of();
        }
        return transactionDAO.findByAccountNo(accountNumber.trim(), startDate, endDate, typeFilter);
    }
}
