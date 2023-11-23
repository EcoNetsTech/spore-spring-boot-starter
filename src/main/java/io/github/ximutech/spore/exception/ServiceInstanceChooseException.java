package io.github.ximutech.spore.exception;

/**
 * 微服务实例选择异常
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
