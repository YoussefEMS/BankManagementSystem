package com.bms.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TransactionRecordBuilder - Factory Method pattern for creating Transaction objects.
 * Centralises the construction logic that was previously duplicated across
 * DepositProcessor, WithdrawCashController, FundsTransferProcessor, and
 * MonthlyInterestPostingCoordinator.
 *
 * Each static factory method encapsulates the type-specific defaults
 * (transaction type string, reference-code prefix, default note) so that
 * callers no longer need to know or repeat those details.
 */
public class TransactionRecordBuilder {

    // Private constructor - utility class, not meant to be instantiated
    private TransactionRecordBuilder() {
    }

    /**
     * Create a Deposit transaction.
     */
    public static Transaction createDeposit(String accountNumber, BigDecimal amount,
            BigDecimal balanceAfter, String performedBy, String description) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(accountNumber);
        tx.setType("Deposit");
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(performedBy);
        tx.setNote(description != null ? description : "Deposit");
        tx.setBalanceAfter(balanceAfter);
        tx.setReferenceCode(generateReference("TXN"));
        return tx;
    }

    /**
     * Create a Withdrawal transaction.
     */
    public static Transaction createWithdrawal(String accountNumber, BigDecimal amount,
            BigDecimal balanceAfter, String performedBy, String description) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(accountNumber);
        tx.setType("Withdrawal");
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(performedBy);
        tx.setNote(description != null ? description : "Withdrawal");
        tx.setBalanceAfter(balanceAfter);
        tx.setReferenceCode(generateReference("TXN"));
        return tx;
    }

    /**
     * Create a TransferDebit transaction (source side of a transfer).
     */
    public static Transaction createTransferDebit(String sourceAccountNumber, BigDecimal amount,
            BigDecimal balanceAfter, String performedBy,
            String destinationAccountNumber, String referenceCode) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(sourceAccountNumber);
        tx.setType("TransferDebit");
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(performedBy);
        tx.setNote("Transfer to " + destinationAccountNumber);
        tx.setBalanceAfter(balanceAfter);
        tx.setReferenceCode(referenceCode);
        return tx;
    }

    /**
     * Create a TransferCredit transaction (destination side of a transfer).
     */
    public static Transaction createTransferCredit(String destinationAccountNumber, BigDecimal amount,
            BigDecimal balanceAfter, String performedBy,
            String sourceAccountNumber, String referenceCode) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(destinationAccountNumber);
        tx.setType("TransferCredit");
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(performedBy);
        tx.setNote("Transfer from " + sourceAccountNumber);
        tx.setBalanceAfter(balanceAfter);
        tx.setReferenceCode(referenceCode);
        return tx;
    }

    /**
     * Create an InterestPosting transaction.
     */
    public static Transaction createInterestPosting(String accountNumber, BigDecimal interest,
            BigDecimal balanceAfter, double monthlyRate) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(accountNumber);
        tx.setType("InterestPosting");
        tx.setAmount(interest);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy("System");
        tx.setNote("Monthly interest at " + String.format("%.4f", monthlyRate * 100) + "% monthly rate");
        tx.setBalanceAfter(balanceAfter);
        tx.setReferenceCode(generateReference("INT"));
        return tx;
    }

    /**
     * Generate a unique reference code with the given prefix.
     * Format: PREFIX-XXXXXXXX (8 uppercase hex chars from UUID)
     */
    private static String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
