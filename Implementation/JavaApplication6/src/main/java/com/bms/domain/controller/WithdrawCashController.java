package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bms.domain.decorator.transaction.AuditDecorator;
import com.bms.domain.decorator.transaction.BasicTransactionProcessor;
import com.bms.domain.decorator.transaction.FeeDecorator;
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
 * WithdrawCashController - UC-06: Process Withdrawal
 * Validates amount, checks funds, updates balance, records transaction
 */
public class WithdrawCashController {
    private static final BigDecimal WITHDRAWAL_FEE = new BigDecimal("5.00");

    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final OverdraftHandler overdraftHandler;
    private final TransactionProcessor transactionProcessor;

    public WithdrawCashController() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public WithdrawCashController(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.overdraftHandler = new OverdraftHandler(factory);
        this.transactionProcessor = new NotificationDecorator(
                new AuditDecorator(
                        new FeeDecorator(
                                new BasicTransactionProcessor(),
                                WITHDRAWAL_FEE,
                                "Cash withdrawal service fee")));
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

        return transactionProcessor.process(context, currentContext -> {
            if (!validateAmount(amount)) {
                return -1;
            }

            if (!validateAccountExistsAndActive(accountNo)) {
                return -2;
            }

            if (!checkSufficientFunds(accountNo, currentContext.getTotalDebitAmount().doubleValue())) {
                return -3;
            }

            Account account = accountDAO.findByAccountNo(accountNo);
            BigDecimal withdrawAmount = currentContext.getRequestedAmount();
            BigDecimal balanceAfterWithdrawal = account.getBalance().subtract(withdrawAmount);
            BigDecimal finalBalance = balanceAfterWithdrawal.subtract(currentContext.getFeeAmount());

            accountDAO.updateBalance(accountNo, finalBalance);

            Transaction cashWithdrawalTx = TransactionFactory.createWithdrawal(
                    accountNo, withdrawAmount, balanceAfterWithdrawal, currentContext.getPerformedBy(),
                    currentContext.decorateNote(description));
            int txId = transactionDAO.insert(cashWithdrawalTx);

            if (txId <= 0) {
                return txId;
            }

            if (currentContext.hasFee()) {
                Transaction feeTx = TransactionFactory.createWithdrawal(
                        accountNo, currentContext.getFeeAmount(), finalBalance, currentContext.getPerformedBy(),
                        currentContext.getFeeDescription());
                transactionDAO.insert(feeTx);
            }

            if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
                overdraftHandler.checkOverdraft(accountNo, finalBalance.doubleValue(),
                        txId, LocalDateTime.now());
            }

            return txId;
        });
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
