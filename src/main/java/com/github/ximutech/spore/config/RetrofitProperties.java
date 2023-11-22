package com.github.ximutech.spore.config;

import com.github.ximutech.spore.Constants;
import com.github.ximutech.spore.log.GlobalLogProperty;
import com.github.ximutech.spore.retry.GlobalRetryProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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
}
