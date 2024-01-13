package cn.econets.ximutech.spore.log;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

/**
 * 同一个请求的日志聚合在一起打印。
 * @author ximu
 */
public class AggregateLoggingInterceptor extends LoggingInterceptor {

    public AggregateLoggingInterceptor(GlobalLogProperty globalLogProperty) {
        super(globalLogProperty);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        SporeLogging logging = findLogging(chain);
        if (!needLog(logging)) {
            return chain.proceed(chain.request());
        }
        LogLevel logLevel = logging == null ? globalLogProperty.getLogLevel() : logging.logLevel();
        LogStrategy logStrategy = logging == null ? globalLogProperty.getLogStrategy() : logging.logStrategy();
        BufferingLogger bufferingLogger = new BufferingLogger(matchLogger(logLevel));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(bufferingLogger)
                .setLevel(HttpLoggingInterceptor.Level.valueOf(logStrategy.name()));
        Response response = httpLoggingInterceptor.intercept(chain);
        bufferingLogger.flush();
        return response;
    }

    private static class BufferingLogger implements HttpLoggingInterceptor.Logger {

        private StringBuilder buffer = new StringBuilder(System.lineSeparator());

        private final HttpLoggingInterceptor.Logger delegate;

        public BufferingLogger(HttpLoggingInterceptor.Logger delegate) {
            this.delegate = delegate;
        }

        @Override
        public void log(String message) {
            buffer.append(message).append(System.lineSeparator());
        }

        public void flush() {
            delegate.log(buffer.toString());
            buffer = new StringBuilder(System.lineSeparator());
        }
    }
}
