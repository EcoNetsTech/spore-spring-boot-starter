package io.github.ximutech.spore.config;

import io.github.ximutech.spore.SourceOkHttpClientRegistry;
import io.github.ximutech.spore.log.LoggingInterceptor;
import io.github.ximutech.spore.retry.RetryInterceptor;
import lombok.Data;

/**
 * @author ximu
 */
@Data
public class RetrofitConfigBean {

    private final RetrofitProperties retrofitProperties;

    private RetryInterceptor retryInterceptor;

    private LoggingInterceptor loggingInterceptor;

    private SourceOkHttpClientRegistry sourceOkHttpClientRegistry;

    public RetrofitConfigBean(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }
}
