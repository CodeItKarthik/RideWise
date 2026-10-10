package com.airtribe.ridewise.exception;

public class NoDriverAvailableException extends Exception {
    public NoDriverAvailableException(String message) {
        super(message);
    }

    public NoDriverAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
