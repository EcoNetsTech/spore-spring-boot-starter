package cn.econets.ximutech.spore.test.custom.okhttp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.econets.ximutech.spore.test.TestApplication;
import cn.econets.ximutech.spore.test.entity.HitokotoVO;
import cn.econets.ximutech.spore.test.entity.Result;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author ximu
 */
@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class OkHttpClientTests {

    private MockWebServer mockWebServer;

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Autowired
    CustomOkHttpClientTestApi customOkHttpClientTestApi;

    @Before
    public void before() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);

        HitokotoVO hitokotoVO = new HitokotoVO();
        hitokotoVO.setId(1L);
        hitokotoVO.setHitokoto("测试内容");
        Result<HitokotoVO> result = new Result<>();
        result.setData(hitokotoVO);
        result.setCode(500);
        result.setMsg("");
        MockResponse response  = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(objectMapper.writeValueAsString(result));
        mockWebServer.enqueue(response);
    }

    @After
    public void after() throws IOException {
        mockWebServer.close();
    }

    @Test
    public void test(){
        Result<HitokotoVO> hitokotoVOResult = customOkHttpClientTestApi.get();
        System.out.println(hitokotoVOResult.getData());
    }
}
