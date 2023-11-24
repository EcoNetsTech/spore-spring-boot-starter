package io.github.ximutech.spore.test.decoder;

import io.github.ximutech.spore.SporeClient;
import io.github.ximutech.spore.log.LogStrategy;
import io.github.ximutech.spore.log.SporeLogging;
import io.github.ximutech.spore.test.entity.HitokotoVO;
import io.github.ximutech.spore.test.entity.Result;
import retrofit2.http.GET;

@SporeClient(baseUrl = "http://localhost:8080", errorDecoder = CustomErrorDecoder.class)
@SporeLogging(logStrategy = LogStrategy.BODY)
public interface ErrorDecoderTestApi {

    @GET("/get")
    Result<HitokotoVO> get();
}
