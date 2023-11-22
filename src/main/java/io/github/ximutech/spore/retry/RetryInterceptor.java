package io.github.ximutech.spore.retry;

import io.github.ximutech.spore.exception.RetryFailedException;
import io.github.ximutech.spore.util.AnnotationExtendUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 重试拦截器
 * @author ximu
 */
public class RetryInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(RetryInterceptor.class);

    protected final GlobalRetryProperty globalRetryProperty;

    public RetryInterceptor(GlobalRetryProperty globalRetryProperty) {
        this.globalRetryProperty = globalRetryProperty;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Method method = Objects.requireNonNull(request.tag(Invocation.class)).method();
        // 获取重试配置
        Retry retry = AnnotationExtendUtils.findMergedAnnotation(method, method.getDeclaringClass(), Retry.class);
        if (!needRetry(retry)) {
            return chain.proceed(request);
        }
        // 重试
        int maxRetries = retry == null ? globalRetryProperty.getMaxRetries() : retry.maxRetries();
        int intervalMs = retry == null ? globalRetryProperty.getIntervalMs() : retry.intervalMs();
        RetryRule[] retryRules = retry == null ? globalRetryProperty.getRetryRules() : retry.retryRules();
        return retryIntercept(maxRetries, intervalMs, retryRules, chain);
    }

    protected boolean needRetry(Retry retry) {
        if (globalRetryProperty.isEnable()) {
            if (retry == null) {
                return true;
            }
            return retry.enable();
        } else {
            return retry != null && retry.enable();
        }
    }

    protected Response retryIntercept(int maxRetries, int intervalMs, RetryRule[] retryRules, Chain chain) {
        HashSet<RetryRule> retryRuleSet = (HashSet<RetryRule>)Arrays.stream(retryRules).collect(Collectors.toSet());
        RetryStrategy retryStrategy = new RetryStrategy(maxRetries, intervalMs);
        while (true) {
            try {
                Request request = chain.request();
                Response response = chain.proceed(request);
                // 如果响应状态码是2xx就不用重试，直接返回 response
                if (!retryRuleSet.contains(RetryRule.RESPONSE_STATUS_NOT_2XX) || response.isSuccessful()) {
                    return response;
                } else {
                    if (!retryStrategy.shouldRetry()) {
                        // 最后一次还没成功，返回最后一次response
                        return response;
                    }
                    // 执行重试
                    retryStrategy.retry();
                    logger.debug("The response fails, retry is performed! The response code is " + response.code());
                    response.close();
                }
            } catch (Exception e) {
                if (shouldThrowEx(retryRuleSet, e)) {
                    throw new RuntimeException(e);
                } else {
                    if (!retryStrategy.shouldRetry()) {
                        // 最后一次还没成功，抛出异常
                        throw new RetryFailedException("Retry Failed: Total " + maxRetries
                                + " attempts made at interval " + intervalMs
                                + "ms");
                    }
                    retryStrategy.retry();
                }
            }
        }
    }

    protected boolean shouldThrowEx(HashSet<RetryRule> retryRuleSet, Exception e) {
        if (retryRuleSet.contains(RetryRule.OCCUR_EXCEPTION)) {
            return false;
        }
        if (retryRuleSet.contains(RetryRule.OCCUR_IO_EXCEPTION)) {
            return !(e instanceof IOException);
        }
        return true;
    }

}
