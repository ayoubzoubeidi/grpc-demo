package com.maz.grpcclientdemo;

public interface AccountLookupService {
    AccountLookupSource source();

    AccountDto  lookup(String accountNumber);
}
