package com.github.ximutech.spore.log;

import com.github.ximutech.spore.util.AnnotationExtendUtils;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author ximu
 */
public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    protected final GlobalLogProperty globalLogProperty;

    public LoggingInterceptor(GlobalLogProperty globalLogProperty) {
        this.globalLogProperty = globalLogProperty;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SporeLogging logging = findLogging(chain);
        if (!needLog(logging)) {
            return chain.proceed(chain.request());
        }
        LogLevel logLevel = logging == null ? globalLogProperty.getLogLevel() : logging.logLevel();
        LogStrategy logStrategy = logging == null ? globalLogProperty.getLogStrategy() : logging.logStrategy();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(matchLogger(logLevel))
                .setLevel(HttpLoggingInterceptor.Level.valueOf(logStrategy.name()));
        return httpLoggingInterceptor.intercept(chain);
    }

    protected SporeLogging findLogging(Chain chain) {
        Method method = Objects.requireNonNull(chain.request().tag(Invocation.class)).method();
        return AnnotationExtendUtils.findMergedAnnotation(method, method.getDeclaringClass(), SporeLogging.class);
    }

    protected boolean needLog(SporeLogging logging) {
        if (globalLogProperty.isEnable()) {
            if (logging == null) {
                return true;
            }
            return logging.enable();
        } else {
            return logging != null && logging.enable();
        }
    }

    protected HttpLoggingInterceptor.Logger matchLogger(LogLevel level) {
        if (level == LogLevel.DEBUG) {
            return logger::debug;
        } else if (level == LogLevel.ERROR) {
            return logger::error;
        } else if (level == LogLevel.INFO) {
            return logger::info;
        } else if (level == LogLevel.WARN) {
            return logger::warn;
        } else if (level == LogLevel.TRACE) {
            return logger::trace;
        }
        throw new UnsupportedOperationException("We don't support this log level currently.");
    }
}
