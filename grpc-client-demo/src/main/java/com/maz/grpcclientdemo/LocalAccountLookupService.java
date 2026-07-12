package com.maz.grpcclientdemo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalAccountLookupService implements AccountLookupService {

    @Override
    public AccountLookupSource source() {
        return AccountLookupSource.LOCAL;
    }

    @Override
    public AccountDto lookup(String accountNumber) {
        return new AccountDto(
                accountNumber,
                "TN59" + accountNumber,
                "Local Test User",
                100.0,
                "TND",
                "ACTIVE",
                "SAVINGS",
                "2024-01-01",
                List.of());
    }
}

