package com.hpe.sb.mobile.app.infra.exception;


public class NoConnectionException extends PropelException {

    public NoConnectionException(Throwable e) {
        super(e);
    }

    public NoConnectionException() {
    }
}
