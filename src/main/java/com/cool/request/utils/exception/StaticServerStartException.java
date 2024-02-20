package com.cool.request.utils.exception;

public class StaticServerStartException extends RuntimeException {
    public StaticServerStartException() {
    }

    public StaticServerStartException(Exception e) {
        super(e);
    }
}
