package io.github.ximutech.spore.test.retry;

import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.retry.Retry;
import io.github.ximutech.spore.test.entity.HitokotoVO;
import retrofit2.http.GET;

@SporeClient(baseUrl = "http://localhost:8080", readTimeout = 1, connectTimeout = 1)
@Retry
public interface RetryTestApi {

    @GET("/get")
    HitokotoVO get();
}
