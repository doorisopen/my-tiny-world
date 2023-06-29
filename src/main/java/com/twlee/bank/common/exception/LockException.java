package com.twlee.bank.common.exception;

public class LockException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "시스템 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.";

    public LockException() {
        super(DEFAULT_MESSAGE);
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
