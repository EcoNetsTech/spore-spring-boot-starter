package io.github.ximutech.spore.config;

import io.github.ximutech.spore.SourceOkHttpClientRegistry;
import io.github.ximutech.spore.decoder.ErrorDecoderInterceptor;
import io.github.ximutech.spore.log.LoggingInterceptor;
import io.github.ximutech.spore.retry.RetryInterceptor;
import io.github.ximutech.spore.service.ServiceChooseInterceptor;
import lombok.Data;

/**
 * @author ximu
 */
@Data
public class RetrofitConfigBean {

    private final RetrofitProperties retrofitProperties;

    private RetryInterceptor retryInterceptor;

    private LoggingInterceptor loggingInterceptor;

    private ErrorDecoderInterceptor errorDecoderInterceptor;

    private ServiceChooseInterceptor serviceChooseInterceptor;

    private SourceOkHttpClientRegistry sourceOkHttpClientRegistry;

    public RetrofitConfigBean(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }
}
