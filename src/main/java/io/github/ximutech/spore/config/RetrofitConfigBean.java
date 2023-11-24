package io.github.ximutech.spore.config;

import io.github.ximutech.spore.GlobalInterceptor;
import io.github.ximutech.spore.okhttp.OkHttpClientRegistry;
import io.github.ximutech.spore.decoder.ErrorDecoderInterceptor;
import io.github.ximutech.spore.log.LoggingInterceptor;
import io.github.ximutech.spore.retry.RetryInterceptor;
import io.github.ximutech.spore.service.ServiceChooseInterceptor;
import lombok.Data;
import retrofit2.CallAdapter;
import retrofit2.Converter;

import java.util.Collections;
import java.util.List;

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

    private Class<? extends Converter.Factory>[] globalConverterFactoryClasses;

    private Class<? extends CallAdapter.Factory>[] globalCallAdapterFactoryClasses;

    private OkHttpClientRegistry okHttpClientRegistry;

    private List<GlobalInterceptor> globalInterceptors;

    public RetrofitConfigBean(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }


    public List<GlobalInterceptor> getGlobalInterceptors(){
        if (globalInterceptors == null){
            return Collections.emptyList();
        }
        return globalInterceptors;
    }

}
