package cn.econets.ximutech.spore.test.decoder;

import cn.econets.ximutech.spore.decoder.ErrorDecoder;
import cn.econets.ximutech.spore.exception.ReadResponseBodyException;
import cn.econets.ximutech.spore.util.RetrofitUtils;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义错误解码器
 */
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public RuntimeException invalidRespDecode(Request request, Response response) {
        System.out.println("============ 进入自定义解码器 ============");
        if (response.isSuccessful()) {
            String responseBody = null;
            try {
                responseBody = RetrofitUtils.readResponseBody(response);
            } catch (ReadResponseBodyException e) {
                // do nothing
            }
            throw new RuntimeException("自定义错误码抛异常111");
        }
        return null;
    }

    @Override
    public RuntimeException ioExceptionDecode(Request request, IOException cause) {
        return new RuntimeException("应用网络异常！cause=" + cause);
    }

    @Override
    public RuntimeException exceptionDecode(Request request, Exception cause) {
        return new RuntimeException("其他异常！cause=" + cause);
    }
}
