package com.bms.domain.entity;

import com.bms.domain.controller.LoanInterestCalculator;

/**
 * Shared immutable loan-product metadata used across many comparison scenarios.
 */
public class LoanProductTemplate {
    private final String loanType;
    private final String displayName;
    private final String description;
    private final String ratePolicyLabel;
    private final String eligibilityNotes;
    private final LoanInterestCalculator interestCalculator;

    public LoanProductTemplate(String loanType, String displayName, String description,
            String ratePolicyLabel, String eligibilityNotes, LoanInterestCalculator interestCalculator) {
        this.loanType = loanType;
        this.displayName = displayName;
        this.description = description;
        this.ratePolicyLabel = ratePolicyLabel;
        this.eligibilityNotes = eligibilityNotes;
        this.interestCalculator = interestCalculator;
    }

    public String getLoanType() {
        return loanType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getRatePolicyLabel() {
        return ratePolicyLabel;
    }

    public String getEligibilityNotes() {
        return eligibilityNotes;
    }

    public double calculateRate(double amount, int durationMonths) {
        return interestCalculator.calculateRate(amount, durationMonths);
    }
}
