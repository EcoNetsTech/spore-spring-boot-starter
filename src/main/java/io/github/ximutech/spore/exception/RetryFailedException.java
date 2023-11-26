package io.github.ximutech.spore.exception;

/**
 * 重试失败异常
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
