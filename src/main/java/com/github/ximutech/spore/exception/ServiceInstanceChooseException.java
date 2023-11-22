package com.github.ximutech.spore.exception;

/**
 * @author ximu
 */
public class ServiceInstanceChooseException extends RuntimeException {

    public ServiceInstanceChooseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInstanceChooseException(String message) {
        super(message);
    }

    public ServiceInstanceChooseException(Throwable cause) {
        super(cause);
    }
}
