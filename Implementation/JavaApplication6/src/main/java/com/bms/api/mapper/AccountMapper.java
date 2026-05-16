package com.bms.api.mapper;

import com.bms.api.dto.AccountResponse;
import com.bms.domain.entity.Account;

public final class AccountMapper {
    private AccountMapper() {
    }

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus());
    }
}
