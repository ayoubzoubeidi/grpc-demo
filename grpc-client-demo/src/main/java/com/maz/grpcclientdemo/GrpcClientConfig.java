package com.maz.grpcclientdemo;

import com.maz.grpc.demo.account.v1.AccountServiceGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

@Configuration
@ImportGrpcClients(target = "account-server", types = AccountServiceGrpc.AccountServiceBlockingStub.class)
public class GrpcClientConfig {
}

