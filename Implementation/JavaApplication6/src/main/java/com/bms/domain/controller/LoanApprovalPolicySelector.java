package com.bms.domain.controller;

public class LoanApprovalPolicySelector {

    public LoanApprovalPolicy getPolicy(String customerType) {
        if (customerType == null) {
            throw new IllegalArgumentException("Customer type cannot be null.");
        }

        switch (customerType.trim().toUpperCase()) {
            case "STANDARD":
                return new StandardLoanApprovalPolicy();
            case "PREMIUM":
                return new PremiumLoanApprovalPolicy();
            case "BUSINESS":
                return new BusinessLoanApprovalPolicy();
            default:
                return new StandardLoanApprovalPolicy();
        }
    }
}
