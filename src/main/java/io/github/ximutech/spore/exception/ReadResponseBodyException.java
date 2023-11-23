package io.github.ximutech.spore.exception;

/**
 * @author ximu
 */
public class ReadResponseBodyException extends Exception {

    public ReadResponseBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadResponseBodyException(String message) {
        super(message);
    }

    public ReadResponseBodyException(Throwable cause) {
        super(cause);
    }
}
