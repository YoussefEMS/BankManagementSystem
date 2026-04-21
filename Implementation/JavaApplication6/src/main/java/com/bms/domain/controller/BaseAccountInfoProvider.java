package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import com.bms.domain.entity.Account;

public class BaseAccountInfoProvider implements AccountInfoProvider {
    @Override
    public AccountInfoSnapshot getAccountInfo(Account account) {
        AccountInfoSnapshot view = new AccountInfoSnapshot();
        view.setAccountNumber(account.getAccountNumber());
        view.setStatus(account.getStatus());
        view.setBalance(account.getBalance().toPlainString());
        view.setCurrency(account.getCurrency());
        view.setRewardPoints("");
        view.setWarningMessage("");
        return view;
    }
}
