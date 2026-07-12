package com.maz.grpcclientdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountLookupController {

    private final AccountLookupSelector selector;

    public AccountLookupController(AccountLookupSelector selector) {
        this.selector = selector;
    }

    @GetMapping("/accounts/{accountNumber}")
    public AccountDto getAccount(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "GRPC") AccountLookupSource source) {
        return selector.select(source).lookup(accountNumber);
    }
}
