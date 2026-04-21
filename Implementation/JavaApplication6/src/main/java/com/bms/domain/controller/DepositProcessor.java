package com.bms.domain.controller;

import java.math.BigDecimal;

import com.bms.domain.controller.AuditedTransactionProcessor;
import com.bms.domain.controller.BasicTransactionProcessor;
import com.bms.domain.controller.NotifyingTransactionProcessor;
import com.bms.domain.entity.TransactionContext;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionRecordBuilder;
import com.bms.persistence.AuthContext;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.ConfiguredPersistenceProvider;

/**
 * DepositProcessor - UC-05: Process Deposit
 * Validates amount, looks up account, updates balance, records transaction.
 * Refactored to utilize AbstractTransactionProcessor.
 */
public class DepositProcessor extends AbstractTransactionProcessor<Integer> {

    public DepositProcessor() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public DepositProcessor(PersistenceProvider factory) {
        super(factory, new NotifyingTransactionProcessor(
                new AuditedTransactionProcessor(
                        new BasicTransactionProcessor())));
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

        return executeTransaction(context, description);
    }

    @Override
    protected boolean validateInputs(TransactionContext context) {
        return context.getRequestedAmount().doubleValue() > 0;
    }

    @Override
    protected boolean validateAccountsAndState(TransactionContext context) {
        String accountNo = context.getAccountNumber();
        if (accountNo == null || accountNo.trim().isEmpty()) return false;
        Account account = accountDAO.findByAccountNo(accountNo.trim());
        return account != null && "ACTIVE".equals(account.getStatus());
    }

    @Override
    protected boolean checkSpecificBusinessRules(TransactionContext context) {
        return true; 
    }

    @Override
    protected Integer executeFinancialImpact(TransactionContext context, String description) {
        String accountNo = context.getAccountNumber();
        Account account = accountDAO.findByAccountNo(accountNo);
        
        BigDecimal depositAmount = context.getRequestedAmount();
        BigDecimal newBalance = account.getBalance().add(depositAmount);

        accountDAO.updateBalance(accountNo, newBalance);

        Transaction tx = TransactionRecordBuilder.createDeposit(
                accountNo, depositAmount, newBalance, context.getPerformedBy(),
                context.decorateNote(description));

        return transactionDAO.insert(tx);
    }

    @Override
    protected Integer getFailureResult(int failureStep) {
        // Map template steps to old legacy error codes where possible
        if (failureStep == 1) return -1; // Amount error
        if (failureStep == 2) return -2; // Account error
        return -failureStep;
    }
}
