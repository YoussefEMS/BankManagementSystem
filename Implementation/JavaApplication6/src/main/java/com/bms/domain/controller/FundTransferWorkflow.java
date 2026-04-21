package com.bms.domain.controller;


import com.bms.domain.entity.FundTransferContext;
public interface FundTransferWorkflow {
    FundTransferContext transferFunds(String sourceAccountNo, String destinationAccountNo, 
                                      int customerId, double transferAmount);


    void registerPeer(String peerName, Object peer);
    void unregisterPeer(String peerName);
}
