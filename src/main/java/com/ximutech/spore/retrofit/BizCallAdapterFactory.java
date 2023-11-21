package com.ximutech.spore.retrofit;

import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * retrofit call 适配器
 *
 * @author ximu
 */
public final class BizCallAdapterFactory extends CallAdapter.Factory {
    private final static Logger logger = LoggerFactory.getLogger(BizCallAdapterFactory.class);


    public static BizCallAdapterFactory create() {
        return new BizCallAdapterFactory();
    }

    private BizCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new BodyCallAdapter<>(returnType);

    }

    private static final class BodyCallAdapter<R> implements CallAdapter<R, R> {
        private final Type responseType;

        BodyCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public R adapt(final Call<R> call) {

            Response<R> resp;
            try {
                resp = call.execute();
            } catch (IOException e) {
                logger.warn("可能服务端异常或者地址配置错误", e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                if (resp.isSuccessful()) {
                    return resp.body();
                } else {
                    ResponseBody errorBody = resp.errorBody();
                    if (errorBody != null && errorBody.contentLength() > 0) {
                        throw new RuntimeException(errorBody.string());
                    } else {
                        throw new RuntimeException(resp.raw().toString());
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
