package com.bms.domain.decorator.accountinfo;

import com.bms.domain.entity.Account;

public interface AccountInfoService {
    AccountInfoView getAccountInfo(Account account);
}
