package io.github.ximutech.spore;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描使用@SporeClient注解的所有文件
 * @author ximu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SporeClientsRegistrar.class)
public @interface EnableSporeClients {

    String[] value() default {};

    /**
     * 根据包路径扫描
     */
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
