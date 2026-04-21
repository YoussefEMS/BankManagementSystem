package com.bms.domain.controller;
import com.bms.domain.entity.FundTransferContext;
public interface SourceAccountValidator {
    boolean validateSourceAccountExists(String accountNo, FundTransferContext context);
    boolean validateSufficientFunds(String accountNo, double transferAmount, FundTransferContext context);
    double getSourceAccountBalance(String accountNo);
}
