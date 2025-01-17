package com.justme8code;


import com.justme8code.policies.CustomRetryPolicy;
import com.justme8code.policies.RetryPolicy;

public class Main {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new CustomRetryPolicy(0); // Retry up to 5 times

        String result = TryLite.tryCatch(() -> {
            System.out.println("Executing operation...");
            if (Math.random() > 0.5) {
                throw new ArithmeticException("Random failure");
            }
            return "Success!";
        }, retryPolicy,"An error occurred during execution");

        System.out.println(result);  // Success or failure after retries
    }
}