package com.ximutech.spore.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
