package io.github.ximutech.spore.config;

import io.github.ximutech.spore.SourceOkHttpClientRegistry;
import io.github.ximutech.spore.decoder.ErrorDecoder;
import io.github.ximutech.spore.decoder.ErrorDecoderInterceptor;
import io.github.ximutech.spore.log.LoggingInterceptor;
import io.github.ximutech.spore.SourceOkHttpClientRegistrar;
import io.github.ximutech.spore.retry.RetryInterceptor;
import io.github.ximutech.spore.service.ServiceChooseInterceptor;
import io.github.ximutech.spore.service.ServiceInstanceChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author ximu
 */
@AutoConfiguration
@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitAutoConfiguration {

    private final RetrofitProperties retrofitProperties;

    public RetrofitAutoConfiguration(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }


    @Bean
    @ConditionalOnMissingBean
    public RetryInterceptor retrofitRetryInterceptor() {
        return new RetryInterceptor(retrofitProperties.getGlobalRetry());
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingInterceptor retrofitLoggingInterceptor() {
        return new LoggingInterceptor(retrofitProperties.getGlobalLog());
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceChooseInterceptor serviceChooseInterceptor(@Autowired ServiceInstanceChooser serviceInstanceChooser){
        return new ServiceChooseInterceptor(serviceInstanceChooser);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder.DefaultErrorDecoder retrofitDefaultErrorDecoder() {
        return new ErrorDecoder.DefaultErrorDecoder();
    }
    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoderInterceptor errorDecoderInterceptor(){
        return new ErrorDecoderInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public SourceOkHttpClientRegistry sourceOkHttpClientRegistry(
            @Autowired(required = false) List<SourceOkHttpClientRegistrar> sourceOkHttpClientRegistrars) {
        return new SourceOkHttpClientRegistry(sourceOkHttpClientRegistrars);
    }

    @Bean
    @ConditionalOnMissingBean
    public RetrofitConfigBean retrofitConfigBean(RetryInterceptor retryInterceptor,
                                                 LoggingInterceptor loggingInterceptor,
                                                 ErrorDecoderInterceptor errorDecoderInterceptor,
                                                 ServiceChooseInterceptor serviceChooseInterceptor,
                                                 SourceOkHttpClientRegistry sourceOkHttpClientRegistry){

        RetrofitConfigBean retrofitConfigBean = new RetrofitConfigBean(retrofitProperties);
        retrofitConfigBean.setRetryInterceptor(retryInterceptor);
        retrofitConfigBean.setLoggingInterceptor(loggingInterceptor);
        retrofitConfigBean.setErrorDecoderInterceptor(errorDecoderInterceptor);
        retrofitConfigBean.setServiceChooseInterceptor(serviceChooseInterceptor);

        retrofitConfigBean.setSourceOkHttpClientRegistry(sourceOkHttpClientRegistry);
        return retrofitConfigBean;
    }

}
