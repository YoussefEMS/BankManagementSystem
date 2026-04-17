package com.bms.mediator.fundtransfer;

public interface IFundTransferMediator {
    FundTransferContext transferFunds(String sourceAccountNo, String destinationAccountNo, 
                                      int customerId, double transferAmount);


    void registerPeer(String peerName, Object peer);
    void unregisterPeer(String peerName);
}
