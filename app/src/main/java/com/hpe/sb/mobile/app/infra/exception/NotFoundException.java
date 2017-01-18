package com.hpe.sb.mobile.app.infra.exception;


public class NotFoundException extends PropelException {

    public NotFoundException(Throwable e) {
        super(e);
    }

    public NotFoundException() {
    }
}
