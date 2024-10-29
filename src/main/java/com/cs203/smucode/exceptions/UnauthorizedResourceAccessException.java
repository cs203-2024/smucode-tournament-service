package com.cs203.smucode.exceptions;

public class UnauthorizedResourceAccessException extends RuntimeException {
    public UnauthorizedResourceAccessException(String message) {
        super(message);
    }
}
