package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.domain.entity.Transfer;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.SqlServerDAOFactory;
import com.bms.persistence.TransactionDAO;
import com.bms.persistence.TransferDAO;

/**
 * TransferHandler - UC-07: Transfer Funds
 * Validates accounts, debits source, credits destination, records transactions
 */
public class TransferHandler {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TransferDAO transferDAO;

    public TransferHandler() {
        this(SqlServerDAOFactory.getInstance());
    }

    public TransferHandler(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.transferDAO = factory.createTransferDAO();
    }

    /**
     * Transfer funds between accounts
     * 
     * @return the reference code on success, null on failure
     */
    public String transferFunds(int customerId, String sourceAccountNo,
            String destinationAccountNo, double amount) {
        // Validate amount
        if (!validateAmount(amount)) {
            return null;
        }

        // Validate accounts
        Account sourceAccount = accountDAO.findByAccountNo(sourceAccountNo);
        Account destAccount = accountDAO.findByAccountNo(destinationAccountNo);

        if (sourceAccount == null || destAccount == null) {
            return null;
        }

        if (!"ACTIVE".equals(sourceAccount.getStatus()) || !"ACTIVE".equals(destAccount.getStatus())) {
            return null;
        }

        if (sourceAccountNo.equals(destinationAccountNo)) {
            return null; // Can't transfer to same account
        }

        // Check sufficient funds
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        if (sourceAccount.getBalance().compareTo(transferAmount) < 0) {
            return null;
        }

        // Compute new balances
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(transferAmount);
        BigDecimal newDestBalance = destAccount.getBalance().add(transferAmount);

        // Generate reference code
        String referenceCode = generateReferenceCode();

        // Update balances
        accountDAO.updateBalance(sourceAccountNo, newSourceBalance);
        accountDAO.updateBalance(destinationAccountNo, newDestBalance);

        // Record debit and credit transactions using Factory Method
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";

        Transaction debitTx = TransactionFactory.createTransferDebit(
                sourceAccountNo, transferAmount, newSourceBalance,
                performedBy, destinationAccountNo, referenceCode);
        transactionDAO.insert(debitTx);

        Transaction creditTx = TransactionFactory.createTransferCredit(
                destinationAccountNo, transferAmount, newDestBalance,
                performedBy, sourceAccountNo, referenceCode);
        transactionDAO.insert(creditTx);

        // Record transfer
        Transfer transfer = new Transfer();
        transfer.setFromAccountNo(sourceAccountNo);
        transfer.setToAccountNo(destinationAccountNo);
        transfer.setAmount(amount);
        transfer.setTimestamp(LocalDateTime.now());
        transfer.setReferenceCode(referenceCode);
        transfer.setStatus("COMPLETED");
        transferDAO.insert(transfer);

        return referenceCode;
    }

    private boolean validateAmount(double amount) {
        return amount > 0;
    }

    private String generateReferenceCode() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}