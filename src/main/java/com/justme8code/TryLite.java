package com.justme8code;

import com.justme8code.policies.RetryPolicy;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class TryLite {
    private static final Logger logger = Logger.getLogger(TryLite.class.getName());

    static {
        try {
            LogManager.getLogManager().readConfiguration();
            FileHandler fileHandler = new FileHandler("trylite_error.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            System.err.println("Error initializing log file");
        }
    }

    // Normal tryCatch function for single execution without retries, fallbacks, or multiple exceptions
    public static <T> T tryCatch(Supplier<T> function, String message) {
        try {
            return function.get();
        } catch (Exception e) {
            logError(e, message);
            throw new RuntimeException(message, e);
        }
    }


    // Core method for TryLite with retries
    public static <T> T tryCatch(Supplier<T> function, RetryPolicy retryPolicy, String message) {
        int attempt = 0;
        while (true) {
            try {
                return function.get();
            } catch (Exception e) {
                if (!retryPolicy.shouldRetry(attempt, e)) {
                    throw new RuntimeException(message, e);
                }

                // Log the retry attempt
                System.out.println("Retry attempt #" + (attempt + 1) + " failed: " + e.getMessage());

                // Wait before retrying
                try {
                    Thread.sleep(retryPolicy.getRetryDelay(attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                attempt++;
            }
        }
    }




    // Multiple exception handling
    public static <T> T tryCatch(Supplier<T> function, List<Class<? extends Exception>> exceptionClasses, List<String> messages) {
        try {
            return function.get();
        } catch (Exception e) {
            for (int i = 0; i < exceptionClasses.size(); i++) {
                if (exceptionClasses.get(i).isInstance(e)) {
                    logError(e, messages.get(i));
                    throw new RuntimeException(messages.get(i), e);
                }
            }
            logError(e, "Unexpected error occurred");
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    // Fallback value handling
    public static <T> T tryCatch(Supplier<T> function, Class<? extends Exception> exceptionClass, String message, T fallback) {
        try {
            return function.get();
        } catch (Exception e) {
            logError(e, message);
            if (exceptionClass.isInstance(e)) {
                return fallback; // Return fallback if exception is matched
            } else {
                throw new RuntimeException("Unexpected error occurred", e);
            }
        }
    }

    // Log errors to the console or file
    private static void logError(Exception e, String message) {
        logger.severe(message);
        logger.severe("Exception: " + e.toString());
        e.printStackTrace(System.err);
    }


}


