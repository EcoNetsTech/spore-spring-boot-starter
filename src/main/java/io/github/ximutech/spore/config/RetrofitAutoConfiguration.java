package io.github.ximutech.spore.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ximutech.spore.GlobalInterceptor;
import io.github.ximutech.spore.okhttp.OkHttpClientRegistry;
import io.github.ximutech.spore.decoder.ErrorDecoder;
import io.github.ximutech.spore.decoder.ErrorDecoderInterceptor;
import io.github.ximutech.spore.log.LoggingInterceptor;
import io.github.ximutech.spore.okhttp.OkHttpClientRegistrar;
import io.github.ximutech.spore.retrofit.converter.BaseTypeConverterFactory;
import io.github.ximutech.spore.retry.RetryInterceptor;
import io.github.ximutech.spore.service.ServiceChooseInterceptor;
import io.github.ximutech.spore.service.ServiceInstanceChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
    public BaseTypeConverterFactory basicTypeConverterFactory() {
        return BaseTypeConverterFactory.INSTANCE;
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
    public ServiceInstanceChooser retrofitServiceInstanceChooser() {
        return new ServiceInstanceChooser.NoValidServiceInstanceChooser();
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
    public OkHttpClientRegistry sourceOkHttpClientRegistry(
            @Autowired(required = false) List<OkHttpClientRegistrar> okHttpClientRegistrars) {
        return new OkHttpClientRegistry(okHttpClientRegistrars);
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonConverterFactory retrofitJacksonConverterFactory() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return JacksonConverterFactory.create(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RetrofitConfigBean retrofitConfigBean(RetryInterceptor retryInterceptor,
                                                 LoggingInterceptor loggingInterceptor,
                                                 ErrorDecoderInterceptor errorDecoderInterceptor,
                                                 ServiceChooseInterceptor serviceChooseInterceptor,
                                                 @Autowired(required = false) List<GlobalInterceptor> globalInterceptors,
                                                 OkHttpClientRegistry okHttpClientRegistry){

        RetrofitConfigBean retrofitConfigBean = new RetrofitConfigBean(retrofitProperties);
        retrofitConfigBean.setGlobalCallAdapterFactoryClasses(retrofitProperties.getGlobalCallAdapterFactories());
        retrofitConfigBean.setGlobalConverterFactoryClasses(retrofitProperties.getGlobalConverterFactories());

        retrofitConfigBean.setRetryInterceptor(retryInterceptor);
        retrofitConfigBean.setLoggingInterceptor(loggingInterceptor);
        retrofitConfigBean.setErrorDecoderInterceptor(errorDecoderInterceptor);
        retrofitConfigBean.setServiceChooseInterceptor(serviceChooseInterceptor);

        retrofitConfigBean.setOkHttpClientRegistry(okHttpClientRegistry);

        retrofitConfigBean.setGlobalInterceptors(globalInterceptors);

        return retrofitConfigBean;
    }

}
