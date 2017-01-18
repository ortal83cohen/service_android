package com.hpe.sb.mobile.app.infra.exception;


public class AuthorizationException extends PropelException {

    public AuthorizationException(Throwable e) {
        super(e);
    }

    public AuthorizationException() {
    }
}
