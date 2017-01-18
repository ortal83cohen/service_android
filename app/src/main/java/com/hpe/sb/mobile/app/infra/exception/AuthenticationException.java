package com.hpe.sb.mobile.app.infra.exception;


public class AuthenticationException extends PropelException {

    public AuthenticationException(Throwable e) {
        super(e);
    }

    public AuthenticationException() {
    }
}
