package com.hpe.sb.mobile.app.infra.exception;


public class RequestTimeoutException extends PropelException {

    public RequestTimeoutException(Throwable e) {
        super(e);
    }

}
