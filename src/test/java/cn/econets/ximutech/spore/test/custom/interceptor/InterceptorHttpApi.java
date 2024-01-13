package cn.econets.ximutech.spore.test.custom.interceptor;

import cn.econets.ximutech.spore.test.entity.HitokotoVO;
import cn.econets.ximutech.spore.test.entity.Result;
import cn.econets.ximutech.spore.Intercept;
import cn.econets.ximutech.spore.SporeClient;
import cn.econets.ximutech.spore.log.LogStrategy;
import cn.econets.ximutech.spore.log.SporeLogging;
import retrofit2.http.GET;

@SporeLogging(logStrategy= LogStrategy.HEADERS)
@SporeClient(baseUrl = "http://localhost:8080")
@Intercept(handler = TokenInterceptor.class)
public interface InterceptorHttpApi {
    @GET("/get")
    Result<HitokotoVO> get();
}
