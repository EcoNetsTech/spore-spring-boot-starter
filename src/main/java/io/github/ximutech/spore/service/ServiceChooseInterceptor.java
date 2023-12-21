package io.github.ximutech.spore.service;

import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.util.RetrofitUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Objects;

/**
 * 微服务选择器拦截器 ServiceChooseInterceptor
 * @author ximu
 */
public class ServiceChooseInterceptor implements Interceptor {

    protected final ServiceInstanceChooser serviceInstanceChooser;

    public ServiceChooseInterceptor(ServiceInstanceChooser serviceDiscovery) {
        this.serviceInstanceChooser = serviceDiscovery;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Method method = RetrofitUtils.getMethodFormRequest(request);
        if (method == null) {
            return chain.proceed(request);
        }
        Class<?> declaringClass = method.getDeclaringClass();
        SporeClient retrofitClient =
                AnnotatedElementUtils.findMergedAnnotation(declaringClass, SporeClient.class);
        String baseUrl = retrofitClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            return chain.proceed(request);
        }
        // serviceId服务发现
        String serviceId = retrofitClient.serviceId();
        URI uri = serviceInstanceChooser.choose(serviceId);
        HttpUrl url = request.url();
        HttpUrl newUrl = url.newBuilder()
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .build();
        Request newReq = request.newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(newReq);
    }
}
