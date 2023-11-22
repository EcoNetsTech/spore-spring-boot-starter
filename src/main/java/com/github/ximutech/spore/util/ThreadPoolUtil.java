package com.github.ximutech.spore.util;

public final class ThreadPoolUtil {

    /**
     * 线程池最大数
     */
    public static final int MAXIMUM_POOL_SIZE = 96;

    private ThreadPoolUtil() {
    }

    /**
     * 计算线程数
     * @return int
     */
    public static int calculatePoolSize() {
        double blockingCoefficient = 0.9;
        return poolSize(blockingCoefficient);
    }

    public static int poolSize(double blockingCoefficient) {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        int poolSize = (int) (numberOfCores / (1 - blockingCoefficient));
        return poolSize;
    }
}
