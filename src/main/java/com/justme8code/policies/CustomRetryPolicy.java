package com.justme8code.policies;

public class CustomRetryPolicy implements RetryPolicy{
    private final int maxRetries;

    public CustomRetryPolicy(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean shouldRetry(int attempt, Exception exception) {
        return attempt < maxRetries; // Retry until the maximum attempts are reached
    }

    @Override
    public long getRetryDelay(int attempt) {
        // Exponential backoff: delay increases with each retry
        return (long) Math.pow(2, attempt) * 1000; // Delay in milliseconds
    }
}
