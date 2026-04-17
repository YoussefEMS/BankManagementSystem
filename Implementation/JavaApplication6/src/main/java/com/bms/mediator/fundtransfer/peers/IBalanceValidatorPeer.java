package com.bms.mediator.fundtransfer.peers;

import com.bms.mediator.fundtransfer.FundTransferContext;
public interface IBalanceValidatorPeer {
    boolean validateTransferAmount(double amount, FundTransferContext context);
    boolean wouldCauseNegativeBalance(double currentBalance, double transferAmount);
    double getMaximumTransferAmount();
    double getMinimumTransferAmount();
}
