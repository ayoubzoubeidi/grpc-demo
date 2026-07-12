package com.maz.grpcclientdemo;

import com.maz.grpc.demo.account.v1.AccountReply;
import com.maz.grpc.demo.account.v1.AccountServiceGrpc;
import com.maz.grpc.demo.account.v1.GetAccountRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GrpcAccountLookupService implements AccountLookupService {

    private final AccountServiceGrpc.AccountServiceBlockingStub stub;

    public GrpcAccountLookupService(AccountServiceGrpc.AccountServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public AccountLookupSource source() {
        return AccountLookupSource.GRPC;
    }

    @Override
    public AccountDto lookup(String accountNumber) {
        try {
            GetAccountRequest request = GetAccountRequest.newBuilder()
                    .setAccountNumber(accountNumber)
                    .build();
            AccountReply reply = stub.getAccount(request);
            return toDto(reply);
        } catch (StatusRuntimeException e) {
            throw toAccountLookupException(e);
        }
    }

    private AccountDto toDto(AccountReply reply) {
        return new AccountDto(
                reply.getAccountNumber(),
                reply.getIban(),
                reply.getOwnerFullName(),
                reply.getBalance(),
                reply.getCurrency(),
                reply.getStatus().name(),
                reply.getType().name(),
                reply.getOpenedDate(),
                reply.getLinkedCardNumbersList()
        );
    }

    private AccountLookupException toAccountLookupException(StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();
        HttpStatus httpStatus = switch (code) {
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        String message = e.getStatus().getDescription() != null
                ? e.getStatus().getDescription()
                : code.name();
        return new AccountLookupException(httpStatus, message);
    }
}
