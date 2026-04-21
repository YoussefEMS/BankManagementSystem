package com.bms.domain.controller;

import com.bms.domain.entity.Account;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.ConfiguredPersistenceProvider;

/**
 * AccountStatusUpdater - UC-08: Update Account Status
 * Allows admins to freeze, activate, or close accounts
 */
public class AccountStatusUpdater {
    private final AccountDAO accountDAO;

    public AccountStatusUpdater() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public AccountStatusUpdater(PersistenceProvider factory) {
        this.accountDAO = factory.createAccountDAO();
    }

    /**
     * Update the status of an account
     * 
     * @param adminId   the ID of the admin performing the action
     * @param accountNo the account number
     * @param newStatus the new status (ACTIVE, FROZEN, CLOSED)
     * @return true on success, false on failure
     */
    public boolean updateAccountStatus(int adminId, String accountNo, String newStatus) {
        // Validate new status
        if (!validateNewStatus(newStatus)) {
            return false;
        }

        // Validate account exists
        if (accountNo == null || accountNo.trim().isEmpty()) {
            return false;
        }

        Account account = accountDAO.findByAccountNo(accountNo.trim());
        if (account == null) {
            return false;
        }

        // Don't allow setting same status
        if (newStatus.equals(account.getStatus())) {
            return false;
        }

        // Update status
        return accountDAO.updateStatus(accountNo.trim(), newStatus);
    }

    private boolean validateNewStatus(String newStatus) {
        if (newStatus == null || newStatus.trim().isEmpty())
            return false;
        return newStatus.equals("ACTIVE") || newStatus.equals("FROZEN") || newStatus.equals("CLOSED");
    }
}
