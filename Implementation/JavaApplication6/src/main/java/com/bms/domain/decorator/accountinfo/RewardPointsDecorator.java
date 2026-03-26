package com.bms.domain.decorator.accountinfo;

import java.math.BigDecimal;

import com.bms.domain.entity.Account;

public class RewardPointsDecorator extends AccountInfoDecorator {
    private static final BigDecimal POINT_DIVISOR = new BigDecimal("100");

    public RewardPointsDecorator(AccountInfoService wrappedService) {
        super(wrappedService);
    }

    @Override
    public AccountInfoView getAccountInfo(Account account) {
        AccountInfoView view = super.getAccountInfo(account);
        int rewardPoints = account.getBalance()
                .max(BigDecimal.ZERO)
                .divide(POINT_DIVISOR)
                .intValue();
        view.setRewardPoints(String.valueOf(rewardPoints));
        return view;
    }
}
