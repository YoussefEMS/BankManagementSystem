package com.bms.mediator.fundtransfer.peers;

import com.bms.mediator.fundtransfer.FundTransferContext;

public interface IDestinationAccountValidatorPeer {
    boolean validateDestinationAccountExists(String accountNo, FundTransferContext context);
    boolean validateDestinationCanReceiveFunds(String accountNo, FundTransferContext context);
    boolean areSameCustomerAccounts(String sourceAccountNo, String destinationAccountNo);
}
