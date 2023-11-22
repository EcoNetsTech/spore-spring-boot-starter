package com.github.ximutech.spore.log;

import java.lang.annotation.*;

/**
 * @author ximu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface SporeLogging {

    /**
     * 是否启用日志打印，针对当前接口或者方法
     *
     * @return 是否启用
     */
    boolean enable() default true;

    /**
     * 日志打印级别，支持的日志级别参见{@link LogLevel}
     * 如果为NULL，则取全局日志打印级别
     * <p>
     * Log printing level, see {@link LogLevel} for supported log levels
     *
     * @return 日志打印级别
     */
    LogLevel logLevel() default LogLevel.INFO;

    /**
     * 日志打印策略，支持的日志打印策略参见{@link LogStrategy}
     * 如果为NULL，则取全局日志打印策略
     * <p>
     * Log printing strategy, see {@link LogStrategy} for supported log printing strategies
     *
     * @return 日志打印策略
     */
    LogStrategy logStrategy() default LogStrategy.BASIC;
}
