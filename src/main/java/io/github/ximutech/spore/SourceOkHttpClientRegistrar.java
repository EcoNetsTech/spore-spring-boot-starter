package io.github.ximutech.spore;

/**
 * SourceOkHttpClientRegistry注册器
 *
 * @author ximu
 */
public interface SourceOkHttpClientRegistrar {

    /**
     * 向#{@link SourceOkHttpClientRegistry}注册数据
     *
     * @param registry SourceOkHttpClientRegistry
     */
    void register(SourceOkHttpClientRegistry registry);
}
