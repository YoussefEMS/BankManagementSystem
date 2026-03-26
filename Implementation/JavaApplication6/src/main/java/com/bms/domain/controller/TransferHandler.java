package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bms.domain.decorator.transaction.AuditDecorator;
import com.bms.domain.decorator.transaction.BasicTransactionProcessor;
import com.bms.domain.decorator.transaction.NotificationDecorator;
import com.bms.domain.decorator.transaction.TransactionContext;
import com.bms.domain.decorator.transaction.TransactionProcessor;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.domain.entity.Transfer;
import com.bms.payment.PaymentGateway;
import com.bms.payment.PaymentGatewayException;
import com.bms.payment.PaymentRequest;
import com.bms.payment.PaymentResponse;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.TransactionDAO;
import com.bms.persistence.TransferDAO;

/**
 * TransferHandler - UC-07: Transfer Funds
 * Validates accounts, debits source, credits destination, records transactions.
 * Optionally routes the transfer through an external PaymentGateway adapter
 * (Stripe, PayPal, Square) so that the same workflow works regardless of
 * the third-party payment provider in use.
 */
public class TransferHandler {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TransferDAO transferDAO;
    private final TransactionProcessor transactionProcessor;
    private final PaymentGateway paymentGateway;

    public TransferHandler() {
        this(ConfiguredDAOFactory.getInstance(), null);
    }

    public TransferHandler(DAOFactory factory) {
        this(factory, null);
    }

    public TransferHandler(DAOFactory factory, PaymentGateway paymentGateway) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.transferDAO = factory.createTransferDAO();
        this.transactionProcessor = new NotificationDecorator(
                new AuditDecorator(
                        new BasicTransactionProcessor()));
        this.paymentGateway = paymentGateway;
    }

    /**
     * Transfer funds between accounts
     * 
     * @return the reference code on success, null on failure
     */
    public String transferFunds(int customerId, String sourceAccountNo,
            String destinationAccountNo, double amount) {
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";
        TransactionContext context = new TransactionContext(
                "Transfer",
                sourceAccountNo,
                destinationAccountNo,
                BigDecimal.valueOf(amount),
                performedBy);
        context.addNoteTag("Notification");
        context.addNoteTag("Audit");

        return transactionProcessor.process(context, currentContext -> {
            if (!validateAmount(amount)) {
                return null;
            }

            Account sourceAccount = accountDAO.findByAccountNo(sourceAccountNo);
            Account destAccount = accountDAO.findByAccountNo(destinationAccountNo);

            if (sourceAccount == null || destAccount == null) {
                return null;
            }

            if (!"ACTIVE".equals(sourceAccount.getStatus()) || !"ACTIVE".equals(destAccount.getStatus())) {
                return null;
            }

            if (sourceAccountNo.equals(destinationAccountNo)) {
                return null;
            }

            BigDecimal transferAmount = currentContext.getRequestedAmount();
            if (sourceAccount.getBalance().compareTo(transferAmount) < 0) {
                return null;
            }

            // Route through the external payment gateway adapter if configured
            if (paymentGateway != null) {
                try {
                    String referenceCode = generateReferenceCode();
                    PaymentRequest paymentRequest = new PaymentRequest(
                            referenceCode,
                            transferAmount,
                            sourceAccount.getCurrency(),
                            "Transfer from " + sourceAccountNo + " to " + destinationAccountNo,
                            currentContext.getPerformedBy());
                    PaymentResponse response = paymentGateway.processPayment(paymentRequest);
                    if (!response.isApproved()) {
                        return null;
                    }
                    currentContext.addNoteTag(paymentGateway.getGatewayName());
                } catch (PaymentGatewayException e) {
                    System.err.println("Payment gateway error: " + e.getMessage());
                    return null;
                }
            }

            BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(transferAmount);
            BigDecimal newDestBalance = destAccount.getBalance().add(transferAmount);
            String referenceCode = generateReferenceCode();

            accountDAO.updateBalance(sourceAccountNo, newSourceBalance);
            accountDAO.updateBalance(destinationAccountNo, newDestBalance);

            Transaction debitTx = TransactionFactory.createTransferDebit(
                    sourceAccountNo, transferAmount, newSourceBalance,
                    currentContext.getPerformedBy(), destinationAccountNo, referenceCode);
            debitTx.setNote(currentContext.decorateNote(debitTx.getNote()));
            transactionDAO.insert(debitTx);

            Transaction creditTx = TransactionFactory.createTransferCredit(
                    destinationAccountNo, transferAmount, newDestBalance,
                    currentContext.getPerformedBy(), sourceAccountNo, referenceCode);
            creditTx.setNote(currentContext.decorateNote(creditTx.getNote()));
            transactionDAO.insert(creditTx);

            Transfer transfer = new Transfer();
            transfer.setFromAccountNo(sourceAccountNo);
            transfer.setToAccountNo(destinationAccountNo);
            transfer.setAmount(amount);
            transfer.setTimestamp(LocalDateTime.now());
            transfer.setReferenceCode(referenceCode);
            transfer.setStatus("COMPLETED");
            transferDAO.insert(transfer);

            return referenceCode;
        });
    }

    private boolean validateAmount(double amount) {
        return amount > 0;
    }

    private String generateReferenceCode() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
