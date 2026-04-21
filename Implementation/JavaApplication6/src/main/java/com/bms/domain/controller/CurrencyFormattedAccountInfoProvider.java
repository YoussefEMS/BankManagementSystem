package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import java.util.Locale;

import com.bms.domain.entity.Account;

public class CurrencyFormattedAccountInfoProvider extends AccountInfoEnhancer {
    public CurrencyFormattedAccountInfoProvider(AccountInfoProvider wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoSnapshot getAccountInfo(Account account) {
        AccountInfoSnapshot view = super.getAccountInfo(account);
        view.setBalance(String.format(Locale.US, "%,.2f", account.getBalance()));
        return view;
    }
}
