package io.github.ximutech.spore.decoder;

import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.util.AppContextUtils;
import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 错误解码器拦截器 ErrorDecoderInterceptor
 * @author ximu
 */
public class ErrorDecoderInterceptor implements Interceptor, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    @SneakyThrows
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Method method = Objects.requireNonNull(request.tag(Invocation.class)).method();
        SporeClient retrofitClient =
                AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), SporeClient.class);
        ErrorDecoder errorDecoder =
                AppContextUtils.getBeanOrNew(applicationContext, retrofitClient.errorDecoder());
        boolean decoded = false;
        try {
            Response response = chain.proceed(request);
            if (errorDecoder == null) {
                return response;
            }
            decoded = true;
            Exception exception = errorDecoder.invalidRespDecode(request, response);
            if (exception == null) {
                return response;
            }
            throw exception;
        } catch (IOException e) {
            if (decoded) {
                throw e;
            }
            throw errorDecoder.ioExceptionDecode(request, e);
        } catch (Exception e) {
            if (decoded && e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw errorDecoder.exceptionDecode(request, e);
        }
    }
}
