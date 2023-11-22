package io.github.ximutech.spore;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SporeClient {
    /**
     * 基础URL
     * 可以直接使用url
     * 也可以指定为属性键，例如：$ {propertyKey}
     * 如果baseUrl没有配置，则必须配置serviceId，path可选配置。
     */
    String baseUrl() default "";
    /**
     * 服务id
     * 用于微服务之前的http调用
     * 可以指定为属性键，例如：$ {propertyKey}
     */
    String serviceId() default "";
    /**
     * 服务路径前缀
     */
    String path() default "";

    /**
     * 请求超时时间 单位:毫秒
     */
    long connectTimeout() default 10000;
    /**
     * 读取超时时间 单位:毫秒
     */
    long readTimeout() default 10000;
    /**
     * 写入超时时间 单位:毫秒
     */
    long writeTimeout() default 10000;
    /**
     * 调用超时时间 单位:毫秒
     */
    long callTimeout() default 10000;

    /**
     * 反序列化策略
     *
     * 所有字母均为小写，并在名称元素之间使用下划线作为分隔符  snake_case
     */
    boolean snake() default false;

    /**
     * 请求字符格式
     */
    String charset() default "utf-8";

    /**
     * 是否提前验证Service接口方法
     */
    boolean validateEagerly() default false;

    /**
     * OkHttpClient，根据该名称到#{@link SourceOkHttpClientRegistry}查找对应的OkHttpClient来构建当前接口的OkhttpClient。
     */
    String sourceOkHttpClient() default "";
}
