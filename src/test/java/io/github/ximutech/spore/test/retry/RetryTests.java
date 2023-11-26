package io.github.ximutech.spore.test.retry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ximutech.spore.test.entity.HitokotoVO;
import io.github.ximutech.spore.test.TestApplication;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class RetryTests {

    @Resource
    RetryTestApi retryTestApi;

    private MockWebServer mockWebServer;

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Before
    public void before() throws IOException, InterruptedException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);

        mockWebServer.url("/get");
        HitokotoVO hitokotoVO = new HitokotoVO();
        hitokotoVO.setId(1L);
        hitokotoVO.setHitokoto("测试内容");
        MockResponse response  = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(objectMapper.writeValueAsString(hitokotoVO));
        mockWebServer.enqueue(response);
    }

    @After
    public void after() throws IOException {
        mockWebServer.close();
    }

    @Test
    public void test() {
        HitokotoVO hitokotoVO = retryTestApi.get();
        System.out.println(hitokotoVO);
    }
}
