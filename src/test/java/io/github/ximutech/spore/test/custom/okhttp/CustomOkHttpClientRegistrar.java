package io.github.ximutech.spore.test.custom.okhttp;

import io.github.ximutech.spore.okhttp.OkHttpClientRegistrar;
import io.github.ximutech.spore.okhttp.OkHttpClientRegistry;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

/**
 * 自定义okHttpClient
 * @author ximu
 */
@Component
@Slf4j
public class CustomOkHttpClientRegistrar implements OkHttpClientRegistrar {
    @Override
    public void register(OkHttpClientRegistry registry) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    log.info("========== 自定义okHttpClient的拦截器 ============");
                    return chain.proceed(chain.request());
                })
                .build();

        registry.register("customOkHttpClient", okHttpClient);
    }
}
