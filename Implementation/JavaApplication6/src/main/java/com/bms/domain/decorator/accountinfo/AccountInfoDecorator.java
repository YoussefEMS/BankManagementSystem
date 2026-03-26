package com.bms.domain.decorator.accountinfo;

import com.bms.domain.entity.Account;

public abstract class AccountInfoDecorator implements AccountInfoService {
    protected final AccountInfoService wrappedService;

    protected AccountInfoDecorator(AccountInfoService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public AccountInfoView getAccountInfo(Account account) {
        return wrappedService.getAccountInfo(account);
    }
}
