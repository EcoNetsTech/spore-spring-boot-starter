package cn.econets.ximutech.spore.exception;

/**
 * okhttp解析失败异常
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
