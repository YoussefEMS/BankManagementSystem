package com.bms.domain.controller;

/**
 * AbstractLoanInterestCalculator - Template Method Pattern for loan interest calculation.
 * Provides a standardized framework for calculating loan interest rates based on loan type.
 * Delegates specific rate definitions and loan type identification to concrete subclasses.
 * 
 * This pattern eliminates code duplication across different loan calculator implementations
 * while maintaining flexibility for future enhancements (e.g., variable rates based on conditions).
 */
public abstract class AbstractLoanInterestCalculator implements LoanInterestCalculator {

    /**
     * Template Method: Calculate the interest rate for a loan.
     * Orchestrates the rate determination process by delegating to concrete implementations.
     * 
     * @param amount the loan amount
     * @param durationMonths the loan duration in months
     * @return the interest rate as a percentage (e.g., 15.0 for 15%)
     */
    @Override
    public final double calculateRate(double amount, int durationMonths) {
        return getRate(amount, durationMonths);
    }

    /**
     * Step 1: Define the base interest rate for this loan type.
     * Concrete subclasses override this to provide loan-type-specific rates.
     * 
     * Can be enhanced in future implementations to support variable rates based on:
     * - Loan amount
     * - Duration in months
     * - Credit score
     * - Market conditions
     * 
     * @param amount the loan amount
     * @param durationMonths the loan duration in months
     * @return the interest rate as a percentage
     */
    protected abstract double getRate(double amount, int durationMonths);

    /**
     * Step 2: Get the loan type identifier.
     * Must be implemented by all concrete subclasses.
     * 
     * @return the loan type (e.g., "PERSONAL", "HOME", "AUTO", "DEFAULT")
     */
    @Override
    public abstract String getLoanType();
}
