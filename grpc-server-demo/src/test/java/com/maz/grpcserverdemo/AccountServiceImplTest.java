package com.maz.grpcserverdemo;

import com.maz.grpc.demo.account.v1.AccountReply;
import com.maz.grpc.demo.account.v1.GetAccountRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountServiceImplTest {

    private RandomService random;
    private AccountServiceImpl service;
    private StreamObserver<AccountReply> observer;

    @BeforeEach
    void setUp() {
        Faker faker = spy(new Faker());
        random = mock(RandomService.class);
        doReturn(random).when(faker).random();

        service = new AccountServiceImpl(faker);

        @SuppressWarnings("unchecked")
        StreamObserver<AccountReply> mockObserver = mock(StreamObserver.class);
        observer = mockObserver;
    }


    @Test
    void returnsAccountWhenAccountIsFound() {
        when(random.nextBoolean()).thenReturn(false);

        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountNumber("12345678")
                .build();

        service.getAccount(request, observer);

        ArgumentCaptor<AccountReply> captor = ArgumentCaptor.forClass(AccountReply.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();
        verify(observer, never()).onError(any());

        AccountReply reply = captor.getValue();
        assertThat(reply).isNotNull();
        assertThat(reply.getAccountNumber()).isNotBlank();
        assertThat(reply.getIban()).isNotBlank();
        assertThat(reply.getOwnerFullName()).isNotBlank();
        assertThat(reply.getCurrency()).isNotBlank();
    }

    @Test
    void returnsNotFoundWhenAccountIsMissing() {
        when(random.nextBoolean()).thenReturn(true);

        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountNumber("12345678")
                .build();

        service.getAccount(request, observer);

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(observer).onError(captor.capture());
        verify(observer, never()).onNext(any());
        verify(observer, never()).onCompleted();

        StatusRuntimeException error = (StatusRuntimeException) captor.getValue();
        assertThat(error.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

}