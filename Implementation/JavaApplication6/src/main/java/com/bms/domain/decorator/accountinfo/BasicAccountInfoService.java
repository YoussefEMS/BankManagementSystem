package com.bms.domain.decorator.accountinfo;

import com.bms.domain.entity.Account;

public class BasicAccountInfoService implements AccountInfoService {
    @Override
    public AccountInfoView getAccountInfo(Account account) {
        AccountInfoView view = new AccountInfoView();
        view.setAccountNumber(account.getAccountNumber());
        view.setStatus(account.getStatus());
        view.setBalance(account.getBalance().toPlainString());
        view.setCurrency(account.getCurrency());
        view.setRewardPoints("");
        view.setWarningMessage("");
        return view;
    }
}
