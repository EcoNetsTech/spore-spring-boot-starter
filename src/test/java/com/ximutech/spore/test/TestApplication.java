package com.ximutech.spore.test;

import com.ximutech.spore.EnableSporeClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ximu
 */
@SpringBootApplication
@EnableSporeClients(basePackages = {"com.ximutech.spore"})
@Slf4j
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
