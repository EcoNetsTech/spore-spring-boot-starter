package com.ximutech.spore;

import okhttp3.OkHttpClient;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SourceOkHttpClient注册中心
 *
 * @author ximu
 */
public class SourceOkHttpClientRegistry {

    private final Map<String, OkHttpClient> okHttpClientMap;

    private final List<SourceOkHttpClientRegistrar> registrars;

    public SourceOkHttpClientRegistry(List<SourceOkHttpClientRegistrar> registrars) {
        this.registrars = registrars;
        this.okHttpClientMap = new HashMap<>(4);
    }

    @PostConstruct
    public void init() {
        if (registrars == null) {
            return;
        }
        registrars.forEach(registrar -> registrar.register(this));
    }

    public void register(String name, OkHttpClient okHttpClient) {
        okHttpClientMap.put(name, okHttpClient);
    }

    public OkHttpClient get(String name) {
        OkHttpClient okHttpClient = okHttpClientMap.get(name);
        Assert.notNull(okHttpClient, "Specified OkHttpClient not found! name=" + name);
        return okHttpClient;
    }

}
