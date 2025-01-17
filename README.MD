# TryLite

**TryLite** is a lightweight Java library designed to simplify error handling with features like retries, fallback handling, and support for multiple exceptions. It abstracts repetitive `try-catch` blocks and enhances resilience in applications by offering a more declarative approach.

---

## Features

- **Simple Error Handling**: Replace repetitive `try-catch` blocks with clean and reusable methods.
- **Retries**: Automatically retry failed operations with customizable policies.
- **Fallback Handling**: Provide fallback values for specific exceptions.
- **Multiple Exception Handling**: Handle different exceptions with tailored error messages.
- **Logging**: Logs errors to a file (`trylite_error.log`) for easy debugging.

---

## Installation

### Gradle
Add the following dependency to your `build.gradle` file:
```groovy
implementation 'com.justme8code:trylite:1.0'
```

### Maven
Add this dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>com.justme8code</groupId>
    <artifactId>trylite</artifactId>
    <version>1.0</version>
</dependency>
```

---

## Usage

### 1. Basic Error Handling
Handle errors with a single `tryCatch` method:
```java
import com.justme8code.TryLite;

public class Example {
    public static void main(String[] args) {
        String result = TryLite.tryCatch(() -> riskyOperation(), "An error occurred while executing the operation");
        System.out.println(result);
    }

    private static String riskyOperation() {
        // Some code that might throw an exception
        return "Success!";
    }
}
```

### 2. Retrying Operations
Retry a failed operation with a retry policy:
```java
import com.justme8code.TryLite;
import com.justme8code.RetryPolicy;

public class Example {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new RetryPolicy(3, attempt -> 1000); // Retry 3 times, 1-second delay

        String result = TryLite.tryCatch(
            () -> riskyOperation(),
            retryPolicy,
            "Operation failed after retries"
        );

        System.out.println(result);
    }

    private static String riskyOperation() {
        if (Math.random() < 0.5) {
            throw new RuntimeException("Random failure");
        }
        return "Success after retry!";
    }
}
```

### 3. Fallback Values
Provide a fallback value for specific exceptions:
```java
import com.justme8code.TryLite;

public class Example {
    public static void main(String[] args) {
        String result = TryLite.tryCatch(
            () -> riskyOperation(),
            RuntimeException.class,
            "Operation failed. Using fallback.",
            "Fallback Value"
        );

        System.out.println(result);
    }

    private static String riskyOperation() {
        throw new RuntimeException("Simulated exception");
    }
}
```

### 4. Handling Multiple Exceptions
Handle multiple exceptions with tailored messages:
```java
import com.justme8code.TryLite;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        String result = TryLite.tryCatch(
            () -> riskyOperation(),
            Arrays.asList(IllegalArgumentException.class, NullPointerException.class),
            Arrays.asList("Invalid input provided", "Null value encountered")
        );

        System.out.println(result);
    }

    private static String riskyOperation() {
        throw new NullPointerException("Simulated exception");
    }
}
```

---

## Logging
Errors are automatically logged to a file named `trylite_error.log` located in the application's root directory. Each log entry includes:
- Error message
- Exception details
- Stack trace

---

## Retry Policy
You can define custom retry policies to handle transient issues more effectively:
- **Maximum Retries**: Specify the maximum number of retry attempts.
- **Delay Logic**: Define a custom delay function to control intervals between retries.

Example:
```java
RetryPolicy retryPolicy = new RetryPolicy(5, attempt -> 500 * (attempt + 1));
```
This policy retries up to 5 times with an increasing delay (e.g., 500ms, 1000ms, 1500ms, etc.).

---

## Contributing
Contributions are welcome! If you encounter issues or have suggestions for improvement, feel free to submit a pull request or create an issue in the repository.

---

## License
This project is licensed under the [MIT License](LICENSE).

---

## Contact
For questions or support, reach out to [justme8code](https://github.com/justme8code).

Happy Coding!

