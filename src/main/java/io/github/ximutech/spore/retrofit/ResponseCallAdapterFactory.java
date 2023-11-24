/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.ximutech.spore.retrofit;

import io.github.ximutech.spore.exception.RetrofitException;
import io.github.ximutech.spore.retrofit.adapter.InternalCallAdapterFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Request;
import org.springframework.util.Assert;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 同步调用执行，直接返回 #{@link Response} 对象。
 * <p>
 * Synchronous call execution, directly return #{@link Response} object
 *
 * @author ximu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseCallAdapterFactory extends CallAdapter.Factory implements InternalCallAdapterFactory {

    public static final ResponseCallAdapterFactory INSTANCE = new ResponseCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (Response.class.isAssignableFrom(getRawType(returnType))) {
            return new ResponseCallAdapter<>(returnType);
        }
        return null;
    }

    static final class ResponseCallAdapter<R> implements CallAdapter<R, Response<R>> {

        private final Type returnType;

        ResponseCallAdapter(Type returnType) {
            this.returnType = returnType;
        }

        @Override
        public Type responseType() {
            ParameterizedType parameterizedType = (ParameterizedType)returnType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Assert.notEmpty(actualTypeArguments, "Response must specify generic parameters!");
            return actualTypeArguments[0];
        }

        @Override
        public Response<R> adapt(Call<R> call) {
            Request request = call.request();
            try {
                return call.execute();
            } catch (IOException e) {
                throw Objects.requireNonNull(RetrofitException.errorExecuting(request, e));
            }
        }
    }
}
