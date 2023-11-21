package com.ximutech.spore.retrofit;

import com.ximutech.spore.SporeClient;
import com.ximutech.spore.config.RetrofitConfigBean;
import com.ximutech.spore.util.ApplicationHolder;
import com.ximutech.spore.util.ThreadPoolUtil;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * retrofitClient 工厂
 *
 * @author ximu
 */
public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, InitializingBean, EnvironmentAware, ApplicationContextAware {

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

    private Retrofit createRetrofit(){
        SporeClient sporeClient = AnnotatedElementUtils.findMergedAnnotation(targetClass, SporeClient.class);

        String baseUrl = convertBaseUrl(Objects.requireNonNull(sporeClient));
        OkHttpClient okHttpClient = createOkHttpClient(sporeClient);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .validateEagerly(sporeClient.validateEagerly())
                .addConverterFactory(new HttpConvertFactory(sporeClient.charset(), sporeClient.snake()))
                .addCallAdapterFactory(BizCallAdapterFactory.create())
                .client(okHttpClient);

        return retrofitBuilder.build();
    }

    private String convertBaseUrl(SporeClient sporeClient) {
        String baseurl;
        String value = sporeClient.value();
        String url = null;
        // 读取配置文件的请求地址
        if(environment != null) {
            url = environment.getProperty(value);
        }
        //spring没配置url
        if (StringUtils.isEmpty(url)) {
            //以http开头
            if(!value.startsWith("http")) {
                throw new IllegalArgumentException("config:" + value + " is not define");
            }else{
                baseurl = value;
            }
        }else {
            baseurl = url;
        }
        return baseurl;
    }

    private OkHttpClient createOkHttpClient(SporeClient sporeClient) {
        OkHttpClient.Builder okHttpClientBuilder;
        if (StringUtils.hasText(sporeClient.sourceOkHttpClient())){
            OkHttpClient sourceOkHttpClient = retrofitConfigBean.getSourceOkHttpClientRegistry()
                    .get(sporeClient.sourceOkHttpClient());
            okHttpClientBuilder = sourceOkHttpClient.newBuilder();
        }else {
            okHttpClientBuilder = new OkHttpClient.Builder();

            // 配置请求/响应超时时间
            okHttpClientBuilder.connectTimeout(sporeClient.connectTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.readTimeout(sporeClient.readTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.writeTimeout(sporeClient.writeTimeout(), TimeUnit.MILLISECONDS);
            okHttpClientBuilder.callTimeout(sporeClient.callTimeout(), TimeUnit.MILLISECONDS);

            // 配置线程池
            ConnectionPool pool = new ConnectionPool(180, 5, TimeUnit.MINUTES);
            okHttpClientBuilder.connectionPool(pool);
            okHttpClientBuilder.dispatcher(new Dispatcher(new ThreadPoolExecutor(5,
                    ThreadPoolUtil.MAXIMUM_POOL_SIZE, 3, TimeUnit.MINUTES, new SynchronousQueue<>())));
        }

        // 使用okhttp自带的interceptor打印返回body
        if (logger.isDebugEnabled()) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger::debug);
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(logInterceptor);
        }

        // 注册重试拦截器
        okHttpClientBuilder.addInterceptor(retrofitConfigBean.getRetryInterceptor());
        // 注册日志拦截器
        okHttpClientBuilder.addInterceptor(retrofitConfigBean.getLoggingInterceptor());

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
    public void afterPropertiesSet() {
        //读取spring.application.name，放入header传给下游，当前用于指定应用限流
        String springApplicationName = environment.getProperty("spring.application.name");
        if (!StringUtils.isEmpty(springApplicationName)){
            ApplicationHolder.setSpringApplicationName(springApplicationName);
        }
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
