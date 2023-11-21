package com.ximutech.spore.config;

import com.ximutech.spore.SourceOkHttpClientRegistry;
import com.ximutech.spore.log.LoggingInterceptor;
import com.ximutech.spore.retry.RetryInterceptor;
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
