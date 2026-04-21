package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bms.domain.controller.AuditedTransactionProcessor;
import com.bms.domain.controller.BasicTransactionProcessor;
import com.bms.domain.controller.FeeChargingTransactionProcessor;
import com.bms.domain.controller.NotifyingTransactionProcessor;
import com.bms.domain.entity.TransactionContext;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionRecordBuilder;
import com.bms.persistence.AuthContext;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.ConfiguredPersistenceProvider;

/**
 * WithdrawCashController - UC-06: Process Withdrawal
 * Validates amount, checks funds, updates balance, records transaction.
 * Refactored to utilize AbstractTransactionProcessor.
 */
public class WithdrawCashController extends AbstractTransactionProcessor<Integer> {
    private static final BigDecimal WITHDRAWAL_FEE = new BigDecimal("5.00");
    private final OverdraftMonitor overdraftHandler;

    public WithdrawCashController() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public WithdrawCashController(PersistenceProvider factory) {
        super(factory, new NotifyingTransactionProcessor(
                new AuditedTransactionProcessor(
                        new FeeChargingTransactionProcessor(
                                new BasicTransactionProcessor(),
                                WITHDRAWAL_FEE,
                                "Cash withdrawal service fee"))));
        this.overdraftHandler = new OverdraftMonitor(factory);
    }

    /**
     * Withdraw cash from an account
     * 
     * @return the transaction ID (> 0 on success), negative on failure
     */
    public int withdrawCash(String accountNo, double amount, String description) {
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";
        TransactionContext context = new TransactionContext(
                "Withdrawal",
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
        // Check sufficient funds (RequestedAmount + Fees)
        String accountNo = context.getAccountNumber();
        Account account = accountDAO.findByAccountNo(accountNo);
        if (account == null) return false;
        
        return account.getBalance().compareTo(context.getTotalDebitAmount()) >= 0;
    }

    @Override
    protected Integer executeFinancialImpact(TransactionContext context, String description) {
        String accountNo = context.getAccountNumber();
        Account account = accountDAO.findByAccountNo(accountNo);
        
        BigDecimal withdrawAmount = context.getRequestedAmount();
        BigDecimal balanceAfterWithdrawal = account.getBalance().subtract(withdrawAmount);
        BigDecimal finalBalance = balanceAfterWithdrawal.subtract(context.getFeeAmount());

        accountDAO.updateBalance(accountNo, finalBalance);

        Transaction cashWithdrawalTx = TransactionRecordBuilder.createWithdrawal(
                accountNo, withdrawAmount, balanceAfterWithdrawal, context.getPerformedBy(),
                context.decorateNote(description));
        int txId = transactionDAO.insert(cashWithdrawalTx);

        if (txId <= 0) {
            return txId;
        }

        if (context.hasFee()) {
            Transaction feeTx = TransactionRecordBuilder.createWithdrawal(
                    accountNo, context.getFeeAmount(), finalBalance, context.getPerformedBy(),
                    context.getFeeDescription());
            transactionDAO.insert(feeTx);
        }

        if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
            overdraftHandler.checkOverdraft(accountNo, finalBalance.doubleValue(),
                    txId, LocalDateTime.now());
        }

        return txId;
    }

    @Override
    protected Integer getFailureResult(int failureStep) {
        if (failureStep == 1) return -1; // Amount error
        if (failureStep == 2) return -2; // Account existence/state error
        if (failureStep == 3) return -3; // Insufficient funds
        return -failureStep;
    }

    // Keep public utility methods intact for external usage if they were relied upon
    public boolean validateAccountExistsAndActive(String accountNo) {
        if (accountNo == null || accountNo.trim().isEmpty()) return false;
        Account account = accountDAO.findByAccountNo(accountNo.trim());
        return account != null && "ACTIVE".equals(account.getStatus());
    }

    public boolean validateAmount(double amount) {
        return amount > 0;
    }

    public void authorizeWithdraw(String actorId, String accountNo) {
        // Kept for backward compatibility
    }

    public boolean checkSufficientFunds(String accountNo, double amount) {
        Account account = accountDAO.findByAccountNo(accountNo);
        if (account == null) return false;
        return account.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }
}
