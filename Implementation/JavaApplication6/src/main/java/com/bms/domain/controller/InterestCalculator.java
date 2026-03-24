package com.bms.domain.controller;

import java.math.BigDecimal;

/**
 * Interface for calculating interest on accounts.
 * Part of the Bridge design pattern implementation.
 */
public interface InterestCalculator {

    /**
     * Calculates the interest amount for the given balance.
     * @param balance the current account balance
     * @return the interest amount to be added
     */
    BigDecimal calculate(BigDecimal balance);

    /**
     * Gets the interest rate used for calculation.
     * @return the interest rate as a decimal (e.g., 0.05 for 5%)
     */
    double getRate();
}