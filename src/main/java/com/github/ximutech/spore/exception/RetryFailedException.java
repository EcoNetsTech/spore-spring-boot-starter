package com.github.ximutech.spore.exception;

/**
 * @author ximu
 */
public class RetryFailedException extends RuntimeException {

    public RetryFailedException(String message) {
        super(message);
    }

    public RetryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
