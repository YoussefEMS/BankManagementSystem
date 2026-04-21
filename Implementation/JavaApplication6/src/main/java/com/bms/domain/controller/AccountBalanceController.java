package com.bms.domain.controller;

import com.bms.domain.controller.AccountInfoProvider;
import com.bms.domain.entity.AccountInfoSnapshot;
import com.bms.domain.controller.BaseAccountInfoProvider;
import com.bms.domain.controller.CurrencyFormattedAccountInfoProvider;
import com.bms.domain.controller.OverdraftWarningAccountInfoProvider;
import com.bms.domain.controller.RewardPointsAccountInfoProvider;
import com.bms.domain.entity.Account;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.PersistenceProvider;

/**
 * AccountBalanceController - Domain logic for viewing account balance
 * Implements UC-02: View Account Balance
 * No thrown errors for not-found cases - returns null instead
 */
public class AccountBalanceController {
    private final AccountDAO accountDAO;
    private final AccountInfoProvider accountInfoService;

    public AccountBalanceController() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public AccountBalanceController(PersistenceProvider factory) {
        this.accountDAO = factory.createAccountDAO();
        this.accountInfoService = new RewardPointsAccountInfoProvider(
                new OverdraftWarningAccountInfoProvider(
                        new CurrencyFormattedAccountInfoProvider(
                                new BaseAccountInfoProvider())));
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
    public AccountInfoSnapshot getAccountDetails(String accountNo) {
        Account account = viewAccountSummary(accountNo);

        if (account == null) {
            return null;
        }
        return accountInfoService.getAccountInfo(account);
    }
}
