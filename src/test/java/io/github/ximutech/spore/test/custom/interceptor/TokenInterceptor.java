package io.github.ximutech.spore.test.custom.interceptor;

import io.github.ximutech.spore.BasePathMatchInterceptor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * @author ximu
 */
@Component
@Scope("prototype")
public class TokenInterceptor extends BasePathMatchInterceptor {
    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        System.out.println("============ 进入token拦截器 ===============");
        Request request = chain.request();

        HttpUrl newUrl = request.url().newBuilder()
                .addQueryParameter("token", UUID.randomUUID().toString())
                .build();

        Request newRequest = request.newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(newRequest);
    }
}
