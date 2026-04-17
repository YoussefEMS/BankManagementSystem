package com.bms.mediator.fundtransfer.peers;
import com.bms.mediator.fundtransfer.FundTransferContext;


public interface IAccountStatusHandlerPeer {
    boolean validateAccountsStatus(String sourceAccountNo, String destinationAccountNo, FundTransferContext context);
    String getAccountStatus(String accountNo);
    boolean hasTransferRestrictions(String accountNo);
}
