package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import java.math.BigDecimal;

import com.bms.domain.entity.Account;

public class RewardPointsAccountInfoProvider extends AccountInfoEnhancer {
    private static final BigDecimal POINT_DIVISOR = new BigDecimal("100");

    public RewardPointsAccountInfoProvider(AccountInfoProvider wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoSnapshot getAccountInfo(Account account) {
        AccountInfoSnapshot view = super.getAccountInfo(account);
        int rewardPoints = account.getBalance()
                .max(BigDecimal.ZERO)
                .divide(POINT_DIVISOR)
                .intValue();
        view.setRewardPoints(String.valueOf(rewardPoints));
        return view;
    }
}
