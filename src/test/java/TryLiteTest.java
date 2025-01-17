
import com.justme8code.TryLite;
import com.justme8code.policies.RetryPolicy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TryLiteTest {

    // Test normal tryCatch without retries
    @Test
    public void testNormalTryCatch_Success() {
        String result = TryLite.tryCatch(() -> "Success", "Operation failed");
        assertEquals("Success", result);
    }

    @Test
    public void testNormalTryCatch_Failure() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            TryLite.tryCatch(() -> {
                throw new IllegalArgumentException("Intentional failure");
            }, "Operation failed");
        });
        assertEquals("Operation failed", exception.getMessage());
    }

    // Test tryCatch with retry policy
    @Test
    public void testTryCatchWithRetries_Success() {
        RetryPolicy retryPolicy = new RetryPolicy() {

            @Override
            public boolean shouldRetry(int attempt, Exception exception) {
                int maxRetries = 3;
                return attempt < maxRetries;
            }

            @Override
            public long getRetryDelay(int attempt) {
                return 100; // 100 ms delay
            }
        };

        String result = TryLite.tryCatch(() -> {
            if (Math.random() < 0.3) {
                throw new IllegalStateException("Transient issue");
            }
            return "Success";
        }, retryPolicy, "Operation failed after retries");

        assertEquals("Success", result);
    }

    @Test
    public void testTryCatchWithRetries_Failure() {
        RetryPolicy retryPolicy = new RetryPolicy() {
            private final int maxRetries = 3;

            @Override
            public boolean shouldRetry(int attempt, Exception exception) {
                return attempt < maxRetries;
            }

            @Override
            public long getRetryDelay(int attempt) {
                return 100; // 100 ms delay
            }
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            TryLite.tryCatch(() -> {
                throw new IllegalStateException("Always failing");
            }, retryPolicy, "Operation failed after retries");
        });

        assertEquals("Operation failed after retries", exception.getMessage());
    }

    // Test multiple exception handling
    @Test
    public void testTryCatchWithMultipleExceptions_Matched() {
        List<Class<? extends Exception>> exceptions = Arrays.asList(
                IllegalArgumentException.class,
                IllegalStateException.class
        );
        List<String> messages = Arrays.asList(
                "Caught IllegalArgumentException",
                "Caught IllegalStateException"
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            TryLite.tryCatch(() -> {
                throw new IllegalStateException("Intentional failure");
            }, exceptions, messages);
        });

        assertEquals("Caught IllegalStateException", exception.getMessage());
    }

    @Test
    public void testTryCatchWithMultipleExceptions_Unmatched() {
        List<Class<? extends Exception>> exceptions = Arrays.asList(
                IllegalArgumentException.class,
                IllegalStateException.class
        );
        List<String> messages = Arrays.asList(
                "Caught IllegalArgumentException",
                "Caught IllegalStateException"
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            TryLite.tryCatch(() -> {
                throw new NullPointerException("Unexpected exception");
            }, exceptions, messages);
        });

        assertEquals("Unexpected error occurred", exception.getMessage());
    }

    // Test fallback value handling
    @Test
    public void testTryCatchWithFallback_Matched() {
        String result = TryLite.tryCatch(() -> {
            throw new IllegalArgumentException("Intentional failure");
        }, IllegalArgumentException.class, "Caught exception, returning fallback", "Fallback value");

        assertEquals("Fallback value", result);
    }

    @Test
    public void testTryCatchWithFallback_Unmatched() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            TryLite.tryCatch(() -> {
                throw new IllegalStateException("Unexpected failure");
            }, IllegalArgumentException.class, "Caught exception, returning fallback", "Fallback value");
        });

        assertEquals("Unexpected error occurred", exception.getMessage());
    }


}
