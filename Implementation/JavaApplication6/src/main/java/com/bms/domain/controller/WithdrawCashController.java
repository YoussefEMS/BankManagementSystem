package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.TransactionDAO;

/**
 * WithdrawCashController - UC-06: Process Withdrawal
 * Validates amount, checks funds, updates balance, records transaction
 */
public class WithdrawCashController {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final OverdraftHandler overdraftHandler;

    public WithdrawCashController() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.overdraftHandler = new OverdraftHandler();
    }

    /**
     * Withdraw cash from an account
     * 
     * @return the transaction ID (> 0 on success), negative on failure
     */
    public int withdrawCash(String accountNo, double amount, String description) {
        if (!validateAmount(amount)) {
            return -1;
        }

        if (!validateAccountExistsAndActive(accountNo)) {
            return -2;
        }

        if (!checkSufficientFunds(accountNo, amount)) {
            return -3; // Insufficient funds
        }

        Account account = accountDAO.findByAccountNo(accountNo);
        BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
        BigDecimal newBalance = account.getBalance().subtract(withdrawAmount);

        // Update balance
        accountDAO.updateBalance(accountNo, newBalance);

        // Record the transaction using Factory Method
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";

        Transaction tx = TransactionFactory.createWithdrawal(
                accountNo, withdrawAmount, newBalance, performedBy, description);

        int txId = transactionDAO.insert(tx);

        // Check for overdraft (balance < 0)
        if (newBalance.compareTo(BigDecimal.ZERO) < 0 && txId > 0) {
            overdraftHandler.checkOverdraft(accountNo, newBalance.doubleValue(),
                    txId, LocalDateTime.now());
        }

        return txId;
    }

    public boolean validateAccountExistsAndActive(String accountNo) {
        if (accountNo == null || accountNo.trim().isEmpty())
            return false;
        Account account = accountDAO.findByAccountNo(accountNo.trim());
        return account != null && "ACTIVE".equals(account.getStatus());
    }

    public boolean validateAmount(double amount) {
        return amount > 0;
    }

    public void authorizeWithdraw(String actorId, String accountNo) {
        // Authorization is handled by role-check in the presentation layer
    }

    public boolean checkSufficientFunds(String accountNo, double amount) {
        Account account = accountDAO.findByAccountNo(accountNo);
        if (account == null)
            return false;
        return account.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }
}