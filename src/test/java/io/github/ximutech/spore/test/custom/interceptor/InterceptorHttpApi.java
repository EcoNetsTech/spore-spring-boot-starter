package io.github.ximutech.spore.test.custom.interceptor;

import io.github.ximutech.spore.Intercept;
import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.log.LogStrategy;
import io.github.ximutech.spore.log.SporeLogging;
import io.github.ximutech.spore.test.entity.HitokotoVO;
import io.github.ximutech.spore.test.entity.Result;
import retrofit2.http.GET;

@SporeLogging(logStrategy= LogStrategy.HEADERS)
@SporeClient(baseUrl = "http://localhost:8080")
@Intercept(handler = TokenInterceptor.class)
public interface InterceptorHttpApi {
    @GET("/get")
    Result<HitokotoVO> get();
}
