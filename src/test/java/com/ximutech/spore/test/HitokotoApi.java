package com.ximutech.spore.test;

import com.ximutech.spore.SporeClient;
import retrofit2.http.GET;

//@SporeClient(baseUrl = "${test.baseUrl}")
@SporeClient(baseUrl = "https://v1.hitokoto.cn")
public interface HitokotoApi {

    @GET("/")
    HitokotoVO test();
}
