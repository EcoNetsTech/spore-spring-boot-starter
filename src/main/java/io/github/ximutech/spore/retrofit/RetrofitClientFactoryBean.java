package io.github.ximutech.spore.retrofit;

import io.github.ximutech.spore.Constants;
import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.config.RetrofitConfigBean;
import io.github.ximutech.spore.retrofit.adapter.*;
import io.github.ximutech.spore.util.AppContextUtils;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * retrofitClient 工厂
 *
 * @author ximu
 */
public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitClientFactoryBean.class);

    private final Class<T> targetClass;

    private Environment environment;

    private ApplicationContext applicationContext;

    private RetrofitConfigBean retrofitConfigBean;

    public RetrofitClientFactoryBean(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
        this.retrofitConfigBean = applicationContext.getBean(RetrofitConfigBean.class);
    }

    @Override
    public T getObject() {
        return createRetrofit().create(targetClass);
    }

    /**
     * 创建Retrofit  createRetrofit
     * @return Retrofit
     */
    private Retrofit createRetrofit(){
        SporeClient sporeClient = AnnotatedElementUtils.findMergedAnnotation(targetClass, SporeClient.class);

        String baseUrl = convertBaseUrl(Objects.requireNonNull(sporeClient));
        OkHttpClient okHttpClient = createOkHttpClient(sporeClient);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .validateEagerly(sporeClient.validateEagerly())
//                .addConverterFactory(new HttpConvertFactory(sporeClient.charset(), sporeClient.snake()))
//                .addCallAdapterFactory(BizCallAdapterFactory.create())
                .client(okHttpClient);

        // 添加配置或者指定的ConverterFactory
        List<Class<? extends Converter.Factory>> converterFactories = new ArrayList<>(4);
        converterFactories.addAll(Arrays.asList(sporeClient.converterFactories()));
        converterFactories.addAll(Arrays.asList(retrofitConfigBean.getGlobalConverterFactoryClasses()));
        converterFactories.forEach(converterFactoryClass -> retrofitBuilder
                .addConverterFactory(AppContextUtils.getBeanOrNew(applicationContext, converterFactoryClass)));

        // 添加配置或者指定的CallAdapterFactory
        List<Class<? extends CallAdapter.Factory>> callAdapterFactories = new ArrayList<>(2);
        callAdapterFactories.addAll(Arrays.asList(sporeClient.callAdapterFactories()));
        callAdapterFactories.addAll(Arrays.asList(retrofitConfigBean.getGlobalCallAdapterFactoryClasses()));
        callAdapterFactories.stream()
                // 过滤掉内置的CallAdapterFactory，因为后续会指定add
                .filter(adapterFactoryClass -> !InternalCallAdapterFactory.class.isAssignableFrom(adapterFactoryClass))
                .forEach(adapterFactoryClass -> retrofitBuilder
                        .addCallAdapterFactory(AppContextUtils.getBeanOrNew(applicationContext, adapterFactoryClass)));

        addReactiveCallAdapterFactory(retrofitBuilder);
        retrofitBuilder.addCallAdapterFactory(ResponseCallAdapterFactory.INSTANCE);
        retrofitBuilder.addCallAdapterFactory(BodyCallAdapterFactory.INSTANCE);

        return retrofitBuilder.build();
    }

    private void addReactiveCallAdapterFactory(Retrofit.Builder retrofitBuilder) {
        if (reactor3ClassExist()) {
            retrofitBuilder.addCallAdapterFactory(MonoCallAdapterFactory.INSTANCE);
        }
        if (rxjava2ClassExist()) {
            retrofitBuilder.addCallAdapterFactory(Rxjava2SingleCallAdapterFactory.INSTANCE);
            retrofitBuilder.addCallAdapterFactory(Rxjava2CompletableCallAdapterFactory.INSTANCE);
        }
        if (rxjava3ClassExist()) {
            retrofitBuilder.addCallAdapterFactory(Rxjava3SingleCallAdapterFactory.INSTANCE);
            retrofitBuilder.addCallAdapterFactory(Rxjava3CompletableCallAdapterFactory.INSTANCE);
        }
    }

    private boolean rxjava3ClassExist() {
        try {
            Class.forName("io.reactivex.rxjava3.core.Single");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean rxjava2ClassExist() {
        try {
            Class.forName("io.reactivex.Single");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean reactor3ClassExist() {
        try {
            Class.forName("reactor.core.publisher.Mono");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取baseUrl convertBaseUrl
     * @param sporeClient
     * @return String
     */
    private String convertBaseUrl(SporeClient sporeClient) {
        String baseUrl = sporeClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
            // 解析baseUrl占位符
            if (!baseUrl.endsWith(Constants.SUFFIX)) {
                baseUrl += Constants.SUFFIX;
            }
        } else {
            String serviceId = sporeClient.serviceId();
            String path = sporeClient.path();
            if (!path.endsWith(Constants.SUFFIX)) {
                path += Constants.SUFFIX;
            }
            baseUrl = Constants.HTTP_PREFIX + (serviceId + Constants.SUFFIX + path).replaceAll("/+", Constants.SUFFIX);
            baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
        }
        return baseUrl;
    }

    /**
     * 创建OkHttpClient  createOkHttpClient
     * @param sporeClient
     * @return OkHttpClient
     */
    private OkHttpClient createOkHttpClient(SporeClient sporeClient) {

        OkHttpClient.Builder okHttpClientBuilder;
        // 判断是否使用自定义OkHttpClient
        if (StringUtils.hasText(sporeClient.sourceOkHttpClient())){
            OkHttpClient sourceOkHttpClient = retrofitConfigBean.getOkHttpClientRegistry().get(sporeClient.sourceOkHttpClient());
            okHttpClientBuilder = sourceOkHttpClient.newBuilder();
        }else {
            okHttpClientBuilder = new OkHttpClient.Builder();

            // 配置请求/响应超时时间
            okHttpClientBuilder.connectTimeout(sporeClient.connectTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.readTimeout(sporeClient.readTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.writeTimeout(sporeClient.writeTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.callTimeout(sporeClient.callTimeout(), TimeUnit.MILLISECONDS);

            // 配置线程池
//            ConnectionPool pool = new ConnectionPool(180, 5, TimeUnit.MINUTES);
//            okHttpClientBuilder.connectionPool(pool);
//            okHttpClientBuilder.dispatcher(new Dispatcher(new ThreadPoolExecutor(5,
//                    ThreadPoolUtil.MAXIMUM_POOL_SIZE, 3, TimeUnit.MINUTES, new SynchronousQueue<>())));
        }

        // 注册 微服务选择器拦截器
        if (StringUtils.hasText(sporeClient.serviceId())) {
            okHttpClientBuilder.addInterceptor(retrofitConfigBean.getServiceChooseInterceptor());
        }

        // 注册错误解码拦截器
        okHttpClientBuilder.addInterceptor(retrofitConfigBean.getErrorDecoderInterceptor());
        // 注册重试拦截器
        okHttpClientBuilder.addInterceptor(retrofitConfigBean.getRetryInterceptor());
        // 注册日志拦截器
        okHttpClientBuilder.addInterceptor(retrofitConfigBean.getLoggingInterceptor());

        // 注册全局拦截器
        retrofitConfigBean.getGlobalInterceptors().forEach(okHttpClientBuilder::addInterceptor);

        OkHttpClient httpClient = okHttpClientBuilder.build();
        httpClient.dispatcher().setMaxRequests(1000);
        httpClient.dispatcher().setMaxRequestsPerHost(300);
        return httpClient;
    }

    @Override
    public Class<T> getObjectType() {
        return targetClass;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
