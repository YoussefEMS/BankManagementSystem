package com.bms.domain.controller;

/**
 * LoanInterestCalculator - Bridge interface for loan interest calculation
 * Abstracts the algorithm for calculating interest rates based on loan type
 * Allows different interest strategies without modifying LoanApplicationService
 * Part of the Bridge design pattern implementation
 */
public interface LoanInterestCalculator {

    /**
     * Calculate the interest rate for a loan
     * 
     * @param amount the loan amount
     * @param durationMonths the loan duration in months
     * @return the interest rate as a percentage (e.g., 15.0 for 15%)
     */
    double calculateRate(double amount, int durationMonths);

    /**
     * Get the loan type this calculator handles
     * 
     * @return the loan type (e.g., "PERSONAL", "HOME", "AUTO")
     */
    String getLoanType();
}
