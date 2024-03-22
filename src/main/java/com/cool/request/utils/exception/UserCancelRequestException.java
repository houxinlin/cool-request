package com.cool.request.utils.exception;

public class UserCancelRequestException extends RuntimeException {
    public UserCancelRequestException() {
    }

    public UserCancelRequestException(Exception e) {
        super(e);
    }
}
