package com.bms.mediator.fundtransfer.peers;

import com.bms.mediator.fundtransfer.FundTransferContext;
public interface IOverdraftHandlerPeer {
    boolean isOverdraftAllowed(String accountNo);
    boolean isWithinOverdraftLimit(String accountNo, double transferAmount, double currentBalance, FundTransferContext context);
    double getOverdraftLimit(String accountNo);
    boolean willTriggerOverdraft(String accountNo, double newBalance);
}
