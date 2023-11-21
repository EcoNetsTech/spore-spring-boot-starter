package com.ximutech.spore.test;

import com.ximutech.spore.SporeClient;
import retrofit2.http.GET;

@SporeClient(value = "test.baseUrl")
public interface HitokotoApi {

    @GET("/")
    HitokotoVO test();
}
