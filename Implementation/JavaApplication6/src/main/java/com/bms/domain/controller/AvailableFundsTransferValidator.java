package com.bms.domain.controller;

import java.util.logging.Logger;

import com.bms.domain.entity.FundTransferContext;
import com.bms.domain.controller.TransferBalanceValidator;

public class AvailableFundsTransferValidator implements TransferBalanceValidator {
    private static final Logger logger = Logger.getLogger(AvailableFundsTransferValidator.class.getName());
    
    private static final double MINIMUM_TRANSFER = 0.01;
    private static final double MAXIMUM_TRANSFER = 5000000.0;

    @Override
    public boolean validateTransferAmount(double amount, FundTransferContext context) {
        if (amount < MINIMUM_TRANSFER) {
            logger.warning("Transfer amount below minimum: " + amount);
            return false;
        }

        if (amount > MAXIMUM_TRANSFER) {
            logger.warning("Transfer amount exceeds maximum: " + amount);
            return false;
        }

        if (!isValidCurrency(amount)) {
            logger.warning("Invalid currency amount (precision): " + amount);
            return false;
        }

        logger.info("Transfer amount validation passed: " + amount);
        return true;
    }

    @Override
    public boolean wouldCauseNegativeBalance(double currentBalance, double transferAmount) {
        boolean wouldBeNegative = (currentBalance - transferAmount) < 0;
        logger.info("Negative balance check: Current=" + currentBalance + 
                   ", Transfer=" + transferAmount + 
                   ", Result=" + wouldBeNegative);
        return wouldBeNegative;
    }

    @Override
    public double getMaximumTransferAmount() {
        return MAXIMUM_TRANSFER;
    }

    @Override
    public double getMinimumTransferAmount() {
        return MINIMUM_TRANSFER;
    }

    /**
     * Validates currency precision (2 decimal places max)
     */
    private boolean isValidCurrency(double amount) {
        // Check precision: should not have more than 2 decimal places
        double rounded = Math.round(amount * 100.0) / 100.0;
        return amount == rounded;
    }
}
