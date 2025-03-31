package com.twlee.bank.channel.exception;

public class ChannelConnectionException extends Exception {
    public ChannelConnectionException() {
    }

    public ChannelConnectionException(String message) {
        super(message);
    }

    public ChannelConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelConnectionException(Throwable cause) {
        super(cause);
    }

    public ChannelConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
