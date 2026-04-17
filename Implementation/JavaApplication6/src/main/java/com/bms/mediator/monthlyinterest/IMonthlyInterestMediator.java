package com.bms.mediator.monthlyinterest;

public interface IMonthlyInterestMediator {
    MonthlyInterestContext postMonthlyInterest(java.util.Date runDate);
    void registerPeer(String peerName, Object peer);
    void unregisterPeer(String peerName);
}
