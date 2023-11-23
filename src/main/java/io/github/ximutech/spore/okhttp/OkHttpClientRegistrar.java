package io.github.ximutech.spore.okhttp;

/**
 * SourceOkHttpClientRegistry注册器
 *
 * @author ximu
 */
public interface OkHttpClientRegistrar {

    /**
     * 向#{@link OkHttpClientRegistry}注册数据
     *
     * @param registry SourceOkHttpClientRegistry
     */
    void register(OkHttpClientRegistry registry);
}
