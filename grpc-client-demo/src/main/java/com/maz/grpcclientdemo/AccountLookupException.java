package com.maz.grpcclientdemo;

import org.springframework.http.HttpStatus;

public class AccountLookupException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AccountLookupException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

