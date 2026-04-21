package com.bms.domain.controller;


import com.bms.domain.entity.AccountInfoSnapshot;
import com.bms.domain.entity.Account;

public interface AccountInfoProvider {
    AccountInfoSnapshot getAccountInfo(Account account);
}
