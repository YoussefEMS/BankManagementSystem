package com.bms.domain.decorator.accountinfo;

import java.math.BigDecimal;

import com.bms.domain.entity.Account;

public class OverdraftWarningDecorator extends AccountInfoDecorator {
    public OverdraftWarningDecorator(AccountInfoService wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoView getAccountInfo(Account account) {
        AccountInfoView view = super.getAccountInfo(account);
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            view.setWarningMessage("Account is overdrawn. Immediate review is required.");
        }
        return view;
    }
}
