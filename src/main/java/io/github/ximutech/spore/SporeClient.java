package io.github.ximutech.spore;

import io.github.ximutech.spore.decoder.ErrorDecoder;
import io.github.ximutech.spore.okhttp.OkHttpClientRegistry;

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

    /*========= sourceOkHttpClient为空时超时时间才生效   ===========*/
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
     * OkHttpClient，根据该名称到#{@link OkHttpClientRegistry}查找对应的OkHttpClient来构建当前接口的OkhttpClient。
     */
    String sourceOkHttpClient() default "";

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
     * 当前接口采用的错误解码器，当请求发生异常或者收到无效响应结果的时候，将HTTP相关信息解码到异常中，无效响应由业务自己判断。
     * 一般情况下，每个服务对应的无效响应各不相同，可以自定义对应的{@link ErrorDecoder}，然后配置在这里。
     * <p>
     * The error decoder used in the current interface will decode HTTP related information into the exception when an exception occurs in the request or an invalid response result is received.
     * The invalid response is determined by the business itself.
     * In general, the invalid response corresponding to each service is different, you can customize the corresponding {@link ErrorDecoder}, and then configure it here.
     *
     * @return 错误解码器
     */
    Class<? extends ErrorDecoder> errorDecoder() default ErrorDecoder.DefaultErrorDecoder.class;
}
