package com.bms.domain.decorator.accountinfo;

import java.util.Locale;

import com.bms.domain.entity.Account;

public class CurrencyFormatDecorator extends AccountInfoDecorator {
    public CurrencyFormatDecorator(AccountInfoService wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoView getAccountInfo(Account account) {
        AccountInfoView view = super.getAccountInfo(account);
        view.setBalance(String.format(Locale.US, "%,.2f", account.getBalance()));
        return view;
    }
}
