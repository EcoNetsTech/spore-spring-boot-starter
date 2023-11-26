package io.github.ximutech.spore.test.global.interceptor;

import io.github.ximutech.spore.GlobalInterceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * @author ximu
 */
@Component
public class CustomGlobalInterceptor implements GlobalInterceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        System.out.println("=========== 执行全局拦截器 ===========");

        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .addHeader("traceId", UUID.randomUUID().toString())
                .build();

        return chain.proceed(newRequest);
    }
}
