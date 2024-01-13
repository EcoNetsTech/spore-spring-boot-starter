package cn.econets.ximutech.spore.test.decoder;

import cn.econets.ximutech.spore.SporeClient;
import cn.econets.ximutech.spore.log.LogStrategy;
import cn.econets.ximutech.spore.log.SporeLogging;
import cn.econets.ximutech.spore.test.entity.HitokotoVO;
import cn.econets.ximutech.spore.test.entity.Result;
import retrofit2.http.GET;

@SporeClient(baseUrl = "http://localhost:8080", errorDecoder = CustomErrorDecoder.class, connectTimeoutMs = 20000, readTimeoutMs = 20000)
@SporeLogging(logStrategy = LogStrategy.BASIC)
public interface ErrorDecoderTestApi {

    @GET("/get")
    Result<HitokotoVO> get();
}
