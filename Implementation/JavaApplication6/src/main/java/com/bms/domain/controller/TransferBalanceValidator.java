package com.bms.domain.controller;

import com.bms.domain.entity.FundTransferContext;
public interface TransferBalanceValidator {
    boolean validateTransferAmount(double amount, FundTransferContext context);
    boolean wouldCauseNegativeBalance(double currentBalance, double transferAmount);
    double getMaximumTransferAmount();
    double getMinimumTransferAmount();
}
