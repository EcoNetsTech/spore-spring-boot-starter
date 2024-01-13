package cn.econets.ximutech.spore.test;

import cn.econets.ximutech.spore.EnableSporeClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ximu
 */
@SpringBootApplication
@EnableSporeClients(basePackages = {"cn.econets.ximutech.spore.test"})
@Slf4j
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
