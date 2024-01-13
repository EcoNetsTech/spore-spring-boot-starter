package cn.econets.ximutech.spore.config;

import lombok.Data;

/**
 * 只有在@SporeClient.sourceOkHttpClient为NO_SOURCE_OK_HTTP_CLIENT时才有效
 * @author ximu
 */
@Data
public class GlobalTimeoutProperty {

    /**
     * 全局连接超时时间
     */
    private long connectTimeoutMs = 10000;

    /**
     * 全局读取超时时间
     */
    private long readTimeoutMs = 10000;

    /**
     * 全局写入超时时间
     */
    private long writeTimeoutMs = 10000;

    /**
     * 全局完整调用超时时间
     */
    private long callTimeoutMs = 0;
}
