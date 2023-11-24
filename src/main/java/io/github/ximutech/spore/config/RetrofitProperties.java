package io.github.ximutech.spore.config;

import io.github.ximutech.spore.Constants;
import io.github.ximutech.spore.log.GlobalLogProperty;
import io.github.ximutech.spore.retrofit.BasicTypeConverterFactory;
import io.github.ximutech.spore.retry.GlobalRetryProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author ximu
 */
@ConfigurationProperties(prefix = Constants.RETROFIT)
@Data
public class RetrofitProperties {

    /**
     * 全局的重试配置
     */
    @NestedConfigurationProperty
    private GlobalRetryProperty globalRetry = new GlobalRetryProperty();

    /**
     * 全局日志配置
     */
    @NestedConfigurationProperty
    private GlobalLogProperty globalLog = new GlobalLogProperty();

    /**
     * 全局转换器工厂，转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     * <p>
     * global converter factories, The converter instance is first obtained from the Spring container. If it is not obtained, it is created by reflection.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Converter.Factory>[] globalConverterFactories =
            (Class<? extends Converter.Factory>[])new Class[] {BasicTypeConverterFactory.class,
                    JacksonConverterFactory.class};

    /**
     * 全局调用适配器工厂，转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     * <p>
     * global call adapter factories, The  callAdapter instance is first obtained from the Spring container. If it is not obtained, it is created by reflection.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends CallAdapter.Factory>[] globalCallAdapterFactories = new Class[0];
}
