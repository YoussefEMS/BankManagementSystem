package com.bms.domain.controller;

import com.bms.domain.entity.Account;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.SqlServerDAOFactory;

/**
 * AccountBalanceController - Domain logic for viewing account balance
 * Implements UC-02: View Account Balance
 * No thrown errors for not-found cases - returns null instead
 */
public class AccountBalanceController {
    private final AccountDAO accountDAO;

    public AccountBalanceController() {
        this(SqlServerDAOFactory.getInstance());
    }

    public AccountBalanceController(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
    }

    /**
     * View account summary by account number
     * 
     * @param accountNo the account number to look up
     * @return Account object if found, null if not found (no error thrown)
     */
    public Account viewAccountSummary(String accountNo) {
        // Input validation: allow free-text but trim whitespace
        if (accountNo == null || accountNo.trim().isEmpty()) {
            return null;
        }

        // Call DAO to retrieve account
        Account account = accountDAO.findByAccountNo(accountNo.trim());

        // Return account or null if not found (no error handling)
        return account;
    }

    /**
     * Get account details as strings for presentation layer
     * 
     * @param accountNo the account number to look up
     * @return String array [accountNumber, status, balance, currency] or null if
     *         not found
     */
    public String[] getAccountDetails(String accountNo) {
        Account account = viewAccountSummary(accountNo);

        if (account == null) {
            return null;
        }

        // Format balance to 2 decimal places
        String formattedBalance = String.format("%.2f", account.getBalance());

        return new String[] {
                account.getAccountNumber(),
                account.getStatus(),
                formattedBalance,
                account.getCurrency()
        };
    }
}
