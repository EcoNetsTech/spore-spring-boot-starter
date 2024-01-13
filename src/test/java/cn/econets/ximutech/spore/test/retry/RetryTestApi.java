package cn.econets.ximutech.spore.test.retry;

import cn.econets.ximutech.spore.retry.Retry;
import cn.econets.ximutech.spore.SporeClient;
import cn.econets.ximutech.spore.test.entity.HitokotoVO;
import retrofit2.http.GET;

@SporeClient(baseUrl = "http://localhost:8080", readTimeoutMs = 1, connectTimeoutMs = 1)
@Retry
public interface RetryTestApi {

    @GET("/get")
    HitokotoVO get();
}
