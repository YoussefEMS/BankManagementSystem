package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import java.math.BigDecimal;

import com.bms.domain.entity.Account;

public class OverdraftWarningAccountInfoProvider extends AccountInfoEnhancer {
    public OverdraftWarningAccountInfoProvider(AccountInfoProvider wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoSnapshot getAccountInfo(Account account) {
        AccountInfoSnapshot view = super.getAccountInfo(account);
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            view.setWarningMessage("Account is overdrawn. Immediate review is required.");
        }
        return view;
    }
}
