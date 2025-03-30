package com.twlee.bank.channel.exception;

public class SocketReadException extends RuntimeException {
    public SocketReadException() {
    }

    public SocketReadException(String message) {
        super(message);
    }

    public SocketReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketReadException(Throwable cause) {
        super(cause);
    }

    public SocketReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}