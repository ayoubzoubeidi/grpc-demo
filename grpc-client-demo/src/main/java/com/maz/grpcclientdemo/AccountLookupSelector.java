package com.maz.grpcclientdemo;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountLookupSelector {

    private final Map<AccountLookupSource, AccountLookupService> servicesBySource;

    public AccountLookupSelector(List<AccountLookupService> services) {
        this.servicesBySource = services.stream()
                .collect(Collectors.toMap(AccountLookupService::source, Function.identity()));
    }

    public AccountLookupService select(AccountLookupSource source) {
        AccountLookupService service = servicesBySource.get(source);
        if (service == null) {
            throw new IllegalArgumentException("No AccountLookupService registered for " + source);
        }
        return service;
    }
}

