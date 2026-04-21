package com.bms.strategy.loan;

public class LoanApprovalStrategyFactory {

    public LoanApprovalStrategy getStrategy(String customerType) {
        if (customerType == null) {
            throw new IllegalArgumentException("Customer type cannot be null.");
        }

        switch (customerType.trim().toUpperCase()) {
            case "STANDARD":
                return new StandardLoanApprovalStrategy();
            case "PREMIUM":
                return new PremiumLoanApprovalStrategy();
            case "BUSINESS":
                return new BusinessLoanApprovalStrategy();
            default:
                return new StandardLoanApprovalStrategy();
        }
    }
}