package com.hpe.sb.mobile.app.infra.exception;

public class PropelException extends RuntimeException {

    public PropelException(Throwable e) {
        super(e);
    }

    public PropelException() {
        super();
    }

}
