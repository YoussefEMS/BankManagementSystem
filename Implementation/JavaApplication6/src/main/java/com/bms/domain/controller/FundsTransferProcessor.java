package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bms.domain.controller.AuditedTransactionProcessor;
import com.bms.domain.controller.BasicTransactionProcessor;
import com.bms.domain.controller.NotifyingTransactionProcessor;
import com.bms.domain.entity.TransactionContext;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionRecordBuilder;
import com.bms.domain.entity.Transfer;
import com.bms.domain.controller.PaymentGateway;
import com.bms.domain.controller.PaymentGatewayException;
import com.bms.domain.entity.PaymentRequest;
import com.bms.domain.entity.PaymentResponse;
import com.bms.persistence.AuthContext;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.TransferDAO;

/**
 * FundsTransferProcessor - UC-07: Transfer Funds
 * Validates accounts, debits source, credits destination, records transactions.
 * Refactored to utilize AbstractTransactionProcessor.
 */
public class FundsTransferProcessor extends AbstractTransactionProcessor<String> {
    private final TransferDAO transferDAO;
    private final PaymentGateway paymentGateway;

    public FundsTransferProcessor() {
        this(ConfiguredPersistenceProvider.getInstance(), null);
    }

    public FundsTransferProcessor(PersistenceProvider factory) {
        this(factory, null);
    }

    public FundsTransferProcessor(PersistenceProvider factory, PaymentGateway paymentGateway) {
        super(factory, new NotifyingTransactionProcessor(
                new AuditedTransactionProcessor(
                        new BasicTransactionProcessor())));
        this.transferDAO = factory.createTransferDAO();
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

        return executeTransaction(context, null);
    }

    @Override
    protected boolean validateInputs(TransactionContext context) {
        return context.getRequestedAmount().doubleValue() > 0;
    }

    @Override
    protected boolean validateAccountsAndState(TransactionContext context) {
        String sourceAccountNo = context.getAccountNumber();
        String destinationAccountNo = context.getRelatedAccountNumber();

        if (sourceAccountNo == null || destinationAccountNo == null) return false;
        if (sourceAccountNo.equals(destinationAccountNo)) return false;

        Account sourceAccount = accountDAO.findByAccountNo(sourceAccountNo);
        Account destAccount = accountDAO.findByAccountNo(destinationAccountNo);

        if (sourceAccount == null || destAccount == null) return false;

        return "ACTIVE".equals(sourceAccount.getStatus()) && "ACTIVE".equals(destAccount.getStatus());
    }

    @Override
    protected boolean checkSpecificBusinessRules(TransactionContext context) {
        Account sourceAccount = accountDAO.findByAccountNo(context.getAccountNumber());
        BigDecimal transferAmount = context.getRequestedAmount();
        
        if (sourceAccount.getBalance().compareTo(transferAmount) < 0) {
            return false;
        }

        // Route through the external payment gateway adapter if configured
        if (paymentGateway != null) {
            try {
                String gatewayReference = generateReferenceCode();
                PaymentRequest paymentRequest = new PaymentRequest(
                        gatewayReference,
                        transferAmount,
                        sourceAccount.getCurrency(),
                        "Transfer from " + context.getAccountNumber() + " to " + context.getRelatedAccountNumber(),
                        context.getPerformedBy());
                PaymentResponse response = paymentGateway.processPayment(paymentRequest);
                if (!response.isApproved()) {
                    return false;
                }
                context.addNoteTag(paymentGateway.getGatewayName());
            } catch (PaymentGatewayException e) {
                System.err.println("Payment gateway error: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    @Override
    protected String executeFinancialImpact(TransactionContext context, String description) {
        String sourceAccountNo = context.getAccountNumber();
        String destinationAccountNo = context.getRelatedAccountNumber();

        Account sourceAccount = accountDAO.findByAccountNo(sourceAccountNo);
        Account destAccount = accountDAO.findByAccountNo(destinationAccountNo);
        
        BigDecimal transferAmount = context.getRequestedAmount();
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(transferAmount);
        BigDecimal newDestBalance = destAccount.getBalance().add(transferAmount);
        
        String referenceCode = generateReferenceCode();

        accountDAO.updateBalance(sourceAccountNo, newSourceBalance);
        accountDAO.updateBalance(destinationAccountNo, newDestBalance);

        Transaction debitTx = TransactionRecordBuilder.createTransferDebit(
                sourceAccountNo, transferAmount, newSourceBalance,
                context.getPerformedBy(), destinationAccountNo, referenceCode);
        debitTx.setNote(context.decorateNote(debitTx.getNote()));
        transactionDAO.insert(debitTx);

        Transaction creditTx = TransactionRecordBuilder.createTransferCredit(
                destinationAccountNo, transferAmount, newDestBalance,
                context.getPerformedBy(), sourceAccountNo, referenceCode);
        creditTx.setNote(context.decorateNote(creditTx.getNote()));
        transactionDAO.insert(creditTx);

        Transfer transfer = new Transfer();
        transfer.setFromAccountNo(sourceAccountNo);
        transfer.setToAccountNo(destinationAccountNo);
        transfer.setAmount(transferAmount.doubleValue());
        transfer.setTimestamp(LocalDateTime.now());
        transfer.setReferenceCode(referenceCode);
        transfer.setStatus("COMPLETED");
        transferDAO.insert(transfer);

        return referenceCode;
    }

    @Override
    protected String getFailureResult(int failureStep) {
        return null; // Transfer handler originally returned null on any validation or gateway failure
    }

    private String generateReferenceCode() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
