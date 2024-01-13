package cn.econets.ximutech.spore.decoder;

import cn.econets.ximutech.spore.util.AppContextUtils;
import cn.econets.ximutech.spore.SporeClient;
import cn.econets.ximutech.spore.util.RetrofitUtils;
import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.io.IOException;
import java.lang.reflect.Method;

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
    public Response intercept(Chain chain) {
        Request request = chain.request();
        Method method = RetrofitUtils.getMethodFormRequest(request);
        if (method == null) {
            return chain.proceed(request);
        }
        SporeClient retrofitClient = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), SporeClient.class);
        ErrorDecoder errorDecoder = AppContextUtils.getBeanOrNew(applicationContext, retrofitClient.errorDecoder());
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
