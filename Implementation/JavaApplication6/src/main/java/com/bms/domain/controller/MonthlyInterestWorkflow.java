package com.bms.domain.controller;


import com.bms.domain.entity.MonthlyInterestContext;
public interface MonthlyInterestWorkflow {
    MonthlyInterestContext postMonthlyInterest(java.util.Date runDate);
    void registerPeer(String peerName, Object peer);
    void unregisterPeer(String peerName);
}
