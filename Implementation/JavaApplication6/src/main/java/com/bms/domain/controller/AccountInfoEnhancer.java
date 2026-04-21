package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import com.bms.domain.entity.Account;

public abstract class AccountInfoEnhancer implements AccountInfoProvider {
    protected final AccountInfoProvider wrappedService;

    protected AccountInfoEnhancer(AccountInfoProvider wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public AccountInfoSnapshot getAccountInfo(Account account) {
        return wrappedService.getAccountInfo(account);
    }
}
