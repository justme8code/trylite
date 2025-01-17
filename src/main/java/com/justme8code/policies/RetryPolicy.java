package com.justme8code.policies;

public interface RetryPolicy {
    boolean shouldRetry(int attempt, Exception error);
    long getRetryDelay(int attempt);
}
