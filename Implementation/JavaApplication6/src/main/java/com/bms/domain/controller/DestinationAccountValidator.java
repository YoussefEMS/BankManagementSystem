package com.bms.domain.controller;

import com.bms.domain.entity.FundTransferContext;

public interface DestinationAccountValidator {
    boolean validateDestinationAccountExists(String accountNo, FundTransferContext context);
    boolean validateDestinationCanReceiveFunds(String accountNo, FundTransferContext context);
    boolean areSameCustomerAccounts(String sourceAccountNo, String destinationAccountNo);
}
