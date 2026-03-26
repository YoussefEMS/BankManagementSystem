package com.bms.domain.controller;

import java.math.BigDecimal;

import com.bms.domain.decorator.transaction.AuditDecorator;
import com.bms.domain.decorator.transaction.BasicTransactionProcessor;
import com.bms.domain.decorator.transaction.NotificationDecorator;
import com.bms.domain.decorator.transaction.TransactionContext;
import com.bms.domain.decorator.transaction.TransactionProcessor;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.TransactionDAO;

/**
 * DepositHandler - UC-05: Process Deposit
 * Validates amount, looks up account, updates balance, records transaction
 */
public class DepositHandler {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TransactionProcessor transactionProcessor;

    public DepositHandler() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public DepositHandler(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.transactionProcessor = new NotificationDecorator(
                new AuditDecorator(
                        new BasicTransactionProcessor()));
    }

    /**
     * Post a deposit to an account
     * 
     * @return the transaction ID (> 0 on success), 0 or negative on failure
     */
    public int postDeposit(String accountNo, double amount, String description) {
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";
        TransactionContext context = new TransactionContext(
                "Deposit",
                accountNo,
                null,
                BigDecimal.valueOf(amount),
                performedBy);
        context.addNoteTag("Notification");
        context.addNoteTag("Audit");

        return transactionProcessor.process(context, currentContext -> {
            if (!validateAmount(amount)) {
                return -1;
            }

            Account account = getAccount(accountNo);
            if (account == null) {
                return -2;
            }

            if (!"ACTIVE".equals(account.getStatus())) {
                return -3;
            }

            BigDecimal depositAmount = currentContext.getRequestedAmount();
            BigDecimal newBalance = account.getBalance().add(depositAmount);

            accountDAO.updateBalance(accountNo, newBalance);

            Transaction tx = TransactionFactory.createDeposit(
                    accountNo, depositAmount, newBalance, currentContext.getPerformedBy(),
                    currentContext.decorateNote(description));

            return transactionDAO.insert(tx);
        });
    }

    private boolean validateAmount(double amount) {
        return amount > 0;
    }

    private Account getAccount(String accountNo) {
        if (accountNo == null || accountNo.trim().isEmpty())
            return null;
        return accountDAO.findByAccountNo(accountNo.trim());
    }
}
