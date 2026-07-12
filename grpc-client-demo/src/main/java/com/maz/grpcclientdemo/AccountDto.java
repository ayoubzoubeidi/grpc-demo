package com.maz.grpcclientdemo;

import java.util.List;

public record AccountDto(
        String accountNumber,
        String iban,
        String ownerFullName,
        double balance,
        String currency,
        String status,
        String type,
        String openedDate,
        List<String> linkedCardNumbers
) {
}

