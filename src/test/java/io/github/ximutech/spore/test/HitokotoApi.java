package io.github.ximutech.spore.test;

import io.github.ximutech.spore.SporeClient;
import retrofit2.http.GET;

@SporeClient(baseUrl = "${test.baseUrl}")
//@SporeClient(baseUrl = "https://v1.hitokoto.cn")
public interface HitokotoApi {

    @GET("/")
    HitokotoVO test();
}
