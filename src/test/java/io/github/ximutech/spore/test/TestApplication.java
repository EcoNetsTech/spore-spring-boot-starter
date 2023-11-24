package io.github.ximutech.spore.test;

import io.github.ximutech.spore.EnableSporeClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ximu
 */
@SpringBootApplication
@EnableSporeClients(basePackages = {"io.github.ximutech.spore"})
@Slf4j
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
