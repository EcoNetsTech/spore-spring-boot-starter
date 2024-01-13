package cn.econets.ximutech.spore.retrofit.adapter;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.annotations.NonNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import retrofit2.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author ximu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Rxjava2CompletableCallAdapterFactory extends CallAdapter.Factory implements InternalCallAdapterFactory {

    public static final Rxjava2CompletableCallAdapterFactory INSTANCE = new Rxjava2CompletableCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Completable.class) {
            return null;
        }
        return new NonBodyCallAdapter<>();
    }

    private static class NonBodyCallAdapter<R> implements CallAdapter<R, Completable> {

        @Override
        public Type responseType() {
            return Void.class;
        }

        @Override
        public Completable adapt(Call<R> call) {
            return Completable.create(emitter -> call.enqueue(new NonBodyCallBack(emitter)));
        }

        private class NonBodyCallBack implements Callback<R> {

            private final CompletableEmitter emitter;

            public NonBodyCallBack(@NonNull CompletableEmitter emitter) {
                this.emitter = emitter;
            }

            @Override
            public void onResponse(Call<R> call, Response<R> response) {
                if (response.isSuccessful()) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new HttpException(response));
                }
            }

            @Override
            public void onFailure(Call<R> call, Throwable t) {
                emitter.onError(t);
            }
        }
    }
}
