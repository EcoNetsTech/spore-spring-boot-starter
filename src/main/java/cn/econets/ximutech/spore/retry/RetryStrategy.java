package cn.econets.ximutech.spore.retry;

/**
 * 重试机制
 */
class RetryStrategy {
    private int maxRetries;
    private final int intervalMs;

    public RetryStrategy(int maxRetries, int intervalMs) {
        this.maxRetries = maxRetries;
        this.intervalMs = intervalMs;
    }

    public boolean shouldRetry() {
        return maxRetries > 0;
    }

    public void retry() {
        maxRetries--;
        waitUntilNextTry();
    }

    private void waitUntilNextTry() {
        try {
            Thread.sleep(intervalMs);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
