package com.bms.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionContext {
    private final String transactionType;
    private final String accountNumber;
    private final String relatedAccountNumber;
    private final BigDecimal requestedAmount;
    private final String performedBy;
    private BigDecimal feeAmount = BigDecimal.ZERO;
    private String feeDescription = "";
    private final List<String> noteTags = new ArrayList<>();
    private final List<String> auditEntries = new ArrayList<>();

    public TransactionContext(String transactionType, String accountNumber,
            String relatedAccountNumber, BigDecimal requestedAmount, String performedBy) {
        this.transactionType = transactionType;
        this.accountNumber = accountNumber;
        this.relatedAccountNumber = relatedAccountNumber;
        this.requestedAmount = requestedAmount;
        this.performedBy = performedBy;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRelatedAccountNumber() {
        return relatedAccountNumber;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void addFee(BigDecimal feeAmount, String feeDescription) {
        this.feeAmount = this.feeAmount.add(feeAmount);
        this.feeDescription = feeDescription;
        this.noteTags.add("Fee " + feeAmount.toPlainString());
    }

    public boolean hasFee() {
        return feeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalDebitAmount() {
        return requestedAmount.add(feeAmount);
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void addNoteTag(String noteTag) {
        noteTags.add(noteTag);
    }

    public String decorateNote(String baseNote) {
        String safeBaseNote = baseNote == null || baseNote.trim().isEmpty()
                ? transactionType
                : baseNote.trim();
        if (noteTags.isEmpty()) {
            return safeBaseNote;
        }
        return safeBaseNote + " [" + String.join(" | ", noteTags) + "]";
    }

    public void addAuditEntry(String auditEntry) {
        auditEntries.add(auditEntry);
    }

    public List<String> getAuditEntries() {
        return List.copyOf(auditEntries);
    }
}
