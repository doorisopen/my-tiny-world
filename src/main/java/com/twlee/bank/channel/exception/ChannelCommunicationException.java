package com.twlee.bank.channel.exception;

public class ChannelCommunicationException extends Exception {
    public ChannelCommunicationException() {
    }

    public ChannelCommunicationException(String message) {
        super(message);
    }

    public ChannelCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelCommunicationException(Throwable cause) {
        super(cause);
    }

    public ChannelCommunicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
