package com.bms.mediator.fundtransfer.peers;
import com.bms.mediator.fundtransfer.FundTransferContext;
public interface ISourceAccountValidatorPeer {
    boolean validateSourceAccountExists(String accountNo, FundTransferContext context);
    boolean validateSufficientFunds(String accountNo, double transferAmount, FundTransferContext context);
    double getSourceAccountBalance(String accountNo);
}
