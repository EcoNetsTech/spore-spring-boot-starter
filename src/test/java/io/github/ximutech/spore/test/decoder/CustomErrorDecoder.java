package io.github.ximutech.spore.test.decoder;

import io.github.ximutech.spore.decoder.ErrorDecoder;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 自定义错误解码器
 */
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public RuntimeException invalidRespDecode(Request request, Response response) {
        System.out.println("============ 进入自定义解码器 ============");
        System.out.println(response.body());
        return null;
    }
}
