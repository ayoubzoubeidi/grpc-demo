# grpc-demo

Demo project trying out Spring Boot 4.1's native gRPC support
(spring-boot-starter-grpc-server / spring-boot-starter-grpc-client).

## Modules

- **grpc-shared-demo** — proto contract and generated stub/message classes.
  Plain library jar, no Spring dependency.
- **grpc-server-demo** — implements AccountService, serves it over gRPC
  (Netty, port 9090). Returns fake data via datafaker.
- **grpc-client-demo** — REST facade (port 8080). Calls the server via
  gRPC or falls back to a local in-memory implementation, picked at
  request time via `List<AccountLookupService>`.

## Proto

`grpc-shared-demo/src/main/proto/account.proto` defines `AccountService`
with one RPC, `GetAccount` — returns account number, IBAN, owner name,
balance, currency, status, type and linked cards.

## Run

```bash
mvn -pl grpc-shared-demo,grpc-server-demo,grpc-client-demo -am install
mvn -pl grpc-server-demo spring-boot:run   # gRPC on 9090
mvn -pl grpc-client-demo spring-boot:run   # REST on 8080
```

## Usage

```bash
curl "localhost:8080/accounts/12345678?source=GRPC"
curl "localhost:8080/accounts/12345678?source=LOCAL"
```

`source=GRPC` calls the real server over gRPC, `source=LOCAL` stays
in-process with fake data. Both return the same `AccountDto` shape.
Errors (invalid account number, not found, server unavailable) map to
proper HTTP status codes with an RFC 7807 problem body.

You can also hit the server directly with grpcurl:

```bash
grpcurl -plaintext -d '{"account_number":"12345678"}' \
  localhost:9090 com.maz.grpc.demo.account.v1.AccountService/GetAccount
```

## Requirements

JDK 26, Maven