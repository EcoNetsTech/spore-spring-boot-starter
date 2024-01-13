package cn.econets.ximutech.spore.test.custom.okhttp;

import cn.econets.ximutech.spore.log.LogStrategy;
import cn.econets.ximutech.spore.log.SporeLogging;
import cn.econets.ximutech.spore.SporeClient;
import cn.econets.ximutech.spore.test.entity.HitokotoVO;
import cn.econets.ximutech.spore.test.entity.Result;
import retrofit2.http.GET;

@SporeClient(baseUrl = "http://localhost:8080", okHttpClient = "customOkHttpClient")
@SporeLogging(logStrategy = LogStrategy.BODY)
public interface CustomOkHttpClientTestApi {

    @GET("/get")
    Result<HitokotoVO> get();
}
