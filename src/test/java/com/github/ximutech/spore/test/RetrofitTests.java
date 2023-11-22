package com.github.ximutech.spore.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author ximu
 */
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class RetrofitTests {

    @Resource
    HitokotoApi hitokotoApi;

    @Test
    public void test(){
        HitokotoVO hitokotoVO = hitokotoApi.test();
        System.out.println(hitokotoVO);
    }

}
