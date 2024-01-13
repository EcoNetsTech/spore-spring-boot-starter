package cn.econets.ximutech.spore.exception;

/**
 * io异常
 * @author ximu
 */
public class RetrofitIOException extends RetrofitException {

    public RetrofitIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrofitIOException(String message) {
        super(message);
    }

}
