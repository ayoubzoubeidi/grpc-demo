package com.maz.grpcserverdemo;

import com.maz.grpc.demo.account.v1.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {

    private final Faker faker;

    public AccountServiceImpl(Faker faker) {
        this.faker = faker;
    }

    @Override
    public void getAccount(GetAccountRequest request, StreamObserver<AccountReply> responseObserver) {
        String accountNumber = request.getAccountNumber();

        if (accountNumber.isBlank()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("account_number must not be blank")
                            .asRuntimeException());
            return;
        }

        if (accountNumber.length() != 8) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("account_number must be 8 characters")
                            .asRuntimeException());
            return;
        }

        if (faker.random().nextBoolean()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("account not found")
                            .asRuntimeException());
            return;
        }

        AccountStatus[] statuses = {AccountStatus.ACTIVE, AccountStatus.SUSPENDED, AccountStatus.CLOSED};
        AccountType[] types = {AccountType.CHECKING, AccountType.SAVINGS, AccountType.LOAN};

        var openedDate = faker.timeAndDate()
                .past(1500, java.util.concurrent.TimeUnit.DAYS)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        var cards = IntStream.range(0, faker.random().nextInt(1, 3))
                .mapToObj(i -> faker.finance().creditCard())
                .collect(Collectors.toList());

        var reply = AccountReply.newBuilder()
                .setAccountNumber(accountNumber)
                .setIban(faker.finance().iban("TN"))
                .setOwnerFullName(faker.name().fullName())
                .setBalance(Double.parseDouble(faker.commerce().price(50, 50000)))
                .setCurrency("TND")
                .setStatus(statuses[faker.random().nextInt(statuses.length)])
                .setType(types[faker.random().nextInt(types.length)])
                .setOpenedDate(openedDate.format(DateTimeFormatter.ISO_DATE))
                .addAllLinkedCardNumbers(cards)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}